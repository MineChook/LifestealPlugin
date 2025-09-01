package site.thatkid.lifesteal.gui

import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.main.KSpigot
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.items.Heart
import site.thatkid.lifesteal.items.RevivalBeaconItem

class Items(private val plugin: JavaPlugin) {

    fun createGui(player: Player): Inventory {
        // Create a simple GUI using KSpigot
        val gui = kSpigotGUI(GUIType.ONE_BY_FIVE) {
            title = Component.text("Lifesteal Items")

            page(1) {
                button(Slots.RowOneSlotOne, Heart(plugin).createItem()) {
                    player.sendMessage("§eHeart: §7A Heart increases your maximum health by 1.0 (2 HP).")
                }
                button(Slots.RowOneSlotTwo, RevivalBeaconItem(plugin).createItem()) {
                    player.sendMessage("§eRevival Beacon: §7Use this item to revive a player who has lost all their hearts.")
                }
            }
        }
        return gui as Inventory
    }
}