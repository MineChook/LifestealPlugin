package site.thatkid.lifesteal.gui

import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.name
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.managers.Manager

class RevivalGUI(private val plugin: JavaPlugin, private val manager: Manager) {

    fun createRevivalInventory(player: Player): Inventory {
        val bannedPlayers = manager.getBannedPlayersFromHeartLoss()

        // Create a simple GUI using KSpigot
        val gui = Bukkit.createInventory(player, 27, Component.text("Revival Beacon"))

        if (bannedPlayers.isEmpty()) {
            player.sendMessage("§eNo players are currently banned from heart loss!")
            return gui
        }

        for (i in bannedPlayers) {
            gui.addItem(createPlayerHead(plugin.server.getOfflinePlayer(Bukkit.getPlayer(i)!!.uniqueId), i))
        }

        return gui
    }

    fun openRevivalGUI(player: Player) {
        val inv = createRevivalInventory(player)

        if (inv.isEmpty) return
        if (inv.size == 0) return

        player.openInventory(inv)
    }
    
    private fun createPlayerHead(offlinePlayer: OfflinePlayer, playerName: String): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta!!
        
        meta.displayName(Component.text("§e$playerName"))
        meta.lore(listOf(
            Component.text("§7Click to revive this player"),
            Component.text("§c§lWarning: This will consume the Revival Beacon!")
        ))
        
        // Try to set the skull owner
        try {
            if (meta is SkullMeta) {
                meta.owningPlayer = offlinePlayer
            }
        } catch (e: Exception) {
            plugin.logger.warning("Failed to set skull owner for $playerName: ${e.message}")
        }
        
        head.itemMeta = meta
        return head
    }

    val inventoryClick = listen<InventoryClickEvent> { e ->
        if (e.inventory != createRevivalInventory(e.whoClicked as Player)) return@listen
        e.isCancelled = true

        val clickedItem = e.currentItem

        if (clickedItem == null || clickedItem.type.isAir) return@listen

        if (clickedItem.type != Material.PLAYER_HEAD) return@listen

        val meta = clickedItem.itemMeta as SkullMeta
        val bannedPlayers = manager.getBannedPlayersFromHeartLoss()

        for (i in bannedPlayers) {
            if (meta.playerProfile == Bukkit.getPlayer(i)) {
                manager.revivePlayer(i)
            }
        }
    }
}