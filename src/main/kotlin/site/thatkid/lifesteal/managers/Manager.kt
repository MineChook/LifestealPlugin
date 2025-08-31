package site.thatkid.lifesteal.managers

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.items.Heart
import java.util.Date
import java.util.UUID

class Manager(private val plugin: JavaPlugin, private val config: Configuration) {

    // Track players banned specifically from heart loss
    private val heartLossBannedPlayers = mutableSetOf<String>()

    fun addHeart(player: Player) {
        val attribute = player.getAttribute(Attribute.MAX_HEALTH) ?: return
        val maxAllowed = config.getDouble("maxHealth", 40.0)
        val currentMax = attribute.value
        if (currentMax < maxAllowed) {
            val newMax = (currentMax + 1.0).coerceAtMost(maxAllowed)
            attribute.baseValue = newMax
            if (player.health > newMax) {
                player.health = newMax
            }
        }
    }

    fun removeHeart(player: Player) {
        val attribute = player.getAttribute(Attribute.MAX_HEALTH) ?: return
        val currentMax = attribute.value

        if (currentMax <= 2.0) {
            banPlayer(player)
        }

        if (currentMax > 2.0) {
            val newMax = (currentMax - 2.0).coerceAtLeast(2.0)
            attribute.baseValue = newMax
            if (player.health > newMax) {
                player.health = newMax
            }
        }
    }

    fun drainHeart(player: Player) {
        val attribute = player.getAttribute(Attribute.MAX_HEALTH) ?: return
        val currentMax = attribute.value

        if (currentMax <= 2.0) {
            player.sendMessage("You cannot drain any more hearts.")
            return
        } else {
            val newMax = (currentMax - 2.0).coerceAtLeast(2.0)
            attribute.baseValue = newMax
            if (player.health > newMax) {
                player.health = newMax
            }
            player.inventory.addItem(Heart(plugin).createItem())
        }
    }

    private fun banPlayer(player: Player) {
        player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = 6.0
        val oneMonthMillis = 30L * 24 * 60 * 60 * 1000
        val expires = Date(System.currentTimeMillis() + oneMonthMillis)
        
        // Track that this player was banned due to heart loss
        heartLossBannedPlayers.add(player.name)
        
        player.ban("You have been banned for losing all your hearts.", expires, null, true)
    }

    /**
     * Get list of players banned specifically from heart loss
     */
    fun getBannedPlayersFromHeartLoss(): List<String> {
        return heartLossBannedPlayers.toList()
    }

    /**
     * Revive a player who was banned from heart loss (requires active beacon)
     */
    fun revivePlayer(reviverPlayer: Player, playerName: String): Boolean {
        // Check if player has an active revival beacon
        val canRevive = reviverPlayer.persistentDataContainer.has(
            org.bukkit.NamespacedKey(plugin, "can_revive"),
            org.bukkit.persistence.PersistentDataType.BYTE
        )
        
        if (!canRevive) {
            reviverPlayer.sendMessage("§cYou need to activate a revival beacon first!")
            return false
        }
        
        if (!heartLossBannedPlayers.contains(playerName)) {
            return false
        }

        try {
            val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
            if (offlinePlayer.isBanned) {
                offlinePlayer.ban = null // Unban the player
                
                // If player is online, restore their health
                val onlinePlayer = Bukkit.getPlayer(playerName)
                onlinePlayer?.getAttribute(Attribute.MAX_HEALTH)?.baseValue = 20.0
                
                heartLossBannedPlayers.remove(playerName)
                
                // Consume the revival beacon structure
                consumeRevivalBeacon(reviverPlayer)
                
                return true
            }
        } catch (e: Exception) {
            plugin.logger.warning("Failed to revive player $playerName: ${e.message}")
        }

        return false
    }
    
    private fun consumeRevivalBeacon(player: Player) {
        try {
            // Get stored beacon location
            val x = player.persistentDataContainer.get(
                org.bukkit.NamespacedKey(plugin, "beacon_x"),
                org.bukkit.persistence.PersistentDataType.INTEGER
            ) ?: return
            
            val y = player.persistentDataContainer.get(
                org.bukkit.NamespacedKey(plugin, "beacon_y"),
                org.bukkit.persistence.PersistentDataType.INTEGER
            ) ?: return
            
            val z = player.persistentDataContainer.get(
                org.bukkit.NamespacedKey(plugin, "beacon_z"),
                org.bukkit.persistence.PersistentDataType.INTEGER
            ) ?: return
            
            val location = player.world.getBlockAt(x, y, z).location
            
            // Remove the beacon and surrounding blocks
            val beaconBlock = location.world?.getBlockAt(location)
            if (beaconBlock != null) {
                // Remove the beacon
                beaconBlock.type = Material.AIR
                
                // Remove diamond blocks
                val diamondPositions = listOf(
                    location.clone().add(1.0, 0.0, 0.0),
                    location.clone().add(-1.0, 0.0, 0.0),
                    location.clone().add(0.0, 0.0, 1.0),
                    location.clone().add(0.0, 0.0, -1.0)
                )
                
                // Remove netherite blocks
                val netheritePositions = listOf(
                    location.clone().add(1.0, 0.0, 1.0),
                    location.clone().add(-1.0, 0.0, 1.0),
                    location.clone().add(1.0, 0.0, -1.0),
                    location.clone().add(-1.0, 0.0, -1.0)
                )
                
                for (pos in diamondPositions) {
                    pos.world?.getBlockAt(pos)?.type = Material.AIR
                }
                
                for (pos in netheritePositions) {
                    pos.world?.getBlockAt(pos)?.type = Material.AIR
                }
            }
            
            // Clear revival data from player
            player.persistentDataContainer.remove(org.bukkit.NamespacedKey(plugin, "can_revive"))
            player.persistentDataContainer.remove(org.bukkit.NamespacedKey(plugin, "beacon_x"))
            player.persistentDataContainer.remove(org.bukkit.NamespacedKey(plugin, "beacon_y"))
            player.persistentDataContainer.remove(org.bukkit.NamespacedKey(plugin, "beacon_z"))
            
        } catch (e: Exception) {
            plugin.logger.warning("Failed to consume revival beacon: ${e.message}")
        }
    }

}