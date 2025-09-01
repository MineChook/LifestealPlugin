package site.thatkid.lifesteal.managers

import com.google.gson.GsonBuilder
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.items.Heart
import java.util.Date

class Manager(private val plugin: JavaPlugin, private val config: Configuration) {


    data class SaveData(
        var heartLossBannedPlayers: Set<String> = mutableSetOf(),
    )

    private val gson = GsonBuilder().setPrettyPrinting().create()

    private var heartLossBannedPlayers = mutableSetOf<String>()

    fun addHeart(player: Player) {
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
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
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        val currentMax = attribute.value

        if (currentMax <= 2.0) {
            banPlayer(player)
        }

        if (currentMax > 2.0) {
            val newMax = (currentMax - 1.0).coerceAtLeast(2.0)
            attribute.baseValue = newMax
            if (player.health > newMax) {
                player.health = newMax
            }
        }
    }

    fun drainHeart(player: Player) {
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
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
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 6.0
        
        heartLossBannedPlayers.add(player.name)


        player.kick()
    }

    fun getBannedPlayersFromHeartLoss(): List<String> {
        return heartLossBannedPlayers.toList()
    }

    fun revivePlayer(playerName: String): Boolean {
        if (!heartLossBannedPlayers.contains(playerName)) {
            return false
        }

        try {
            val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
            if (offlinePlayer.isBanned) {
                plugin.server.dispatchCommand(plugin.server.consoleSender, "pardon $playerName")

                val onlinePlayer = Bukkit.getPlayer(playerName)
                onlinePlayer?.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 6.0

                heartLossBannedPlayers.remove(playerName)
                
                return true
            }
        } catch (e: Exception) {
            plugin.logger.warning("Failed to revive player $playerName: ${e.message}")
        }

        return false
    }

    fun save() {
        val data = SaveData(
            heartLossBannedPlayers = heartLossBannedPlayers.toSet()
        )
        val json = gson.toJson(data)
        val file = plugin.dataFolder.resolve("save_data.json")
        try {
            file.writeText(json)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to save data: ${e.message}")
        }
    }

    fun load() {
        if (!plugin.dataFolder.exists())  {
            plugin.dataFolder.mkdirs()
        }
        try {
            val json = plugin.dataFolder.resolve("save_data.json").readText()
            val data = gson.fromJson(json, SaveData::class.java)
            heartLossBannedPlayers = data.heartLossBannedPlayers.toSet() as MutableSet<String>
        } catch (e: Exception) {
            plugin.logger.warning("Failed to load data: ${e.message}")
        }
    }
}