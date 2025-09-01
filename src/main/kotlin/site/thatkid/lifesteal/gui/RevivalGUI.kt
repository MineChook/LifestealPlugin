package site.thatkid.lifesteal.gui

import net.axay.kspigot.gui.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.managers.Manager

class RevivalGUI(private val plugin: JavaPlugin, private val manager: Manager) {

    fun openRevivalGUI(player: Player) {
        val bannedPlayers = manager.getBannedPlayersFromHeartLoss()
        
        if (bannedPlayers.isEmpty()) {
            player.sendMessage("§eNo players are currently banned from heart loss!")
            return
        }

        // Create a simple GUI using KSpigot
        val gui = kSpigotGUI(GUIType.SIX_BY_NINE) {
            title = "§6Revival Menu - Select Player"
            
            page(1) {
                placeholder(Slots.All, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                
                // Add player heads starting from slot 10
                var currentSlot = 10
                for (playerName in bannedPlayers.take(28)) { // Limit to 28 players to fit in GUI
                    val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
                    val playerHead = createPlayerHead(offlinePlayer, playerName)
                    
                    button(currentSlot, playerHead) {
                        it.player.closeInventory()
                        
                        val success = manager.revivePlayer(it.player, playerName)
                        if (success) {
                            it.player.sendMessage("§aSuccessfully revived player $playerName!")
                            // Remove the revival beacon item from player's hand
                            val itemInHand = it.player.inventory.itemInMainHand
                            if (itemInHand.amount > 1) {
                                itemInHand.amount = itemInHand.amount - 1
                            } else {
                                it.player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                            }
                        } else {
                            it.player.sendMessage("§cFailed to revive player $playerName!")
                        }
                    }
                    
                    currentSlot++
                    if (currentSlot % 9 == 8) currentSlot += 2 // Skip to next row, avoiding edges
                }
                
                // Close button at bottom center
                button(49, ItemStack(Material.BARRIER).apply {
                    val meta = itemMeta!!
                    meta.setDisplayName("§cClose")
                    meta.lore = listOf("§7Click to close this menu")
                    itemMeta = meta
                }) {
                    it.player.closeInventory()
                }
            }
        }
        
        gui.openForPlayer(player)
    }
    
    private fun createPlayerHead(offlinePlayer: OfflinePlayer, playerName: String): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta!!
        
        meta.setDisplayName("§e$playerName")
        meta.lore = listOf(
            "§7Click to revive this player",
            "§c§lWarning: This will consume the Revival Beacon!"
        )
        
        // Try to set the skull owner
        try {
            if (meta is org.bukkit.inventory.meta.SkullMeta) {
                meta.owningPlayer = offlinePlayer
            }
        } catch (e: Exception) {
            plugin.logger.warning("Failed to set skull owner for $playerName: ${e.message}")
        }
        
        head.itemMeta = meta
        return head
    }
}