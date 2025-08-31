package site.thatkid.lifesteal.listeners

import net.axay.kspigot.event.listen
import net.axay.kspigot.event.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.items.Heart
import site.thatkid.lifesteal.items.RevivalBeaconItem
import site.thatkid.lifesteal.managers.Manager
import site.thatkid.lifesteal.gui.RevivalGUI

class Listeners(private val plugin: JavaPlugin, private val manager: Manager) {

    private val revivalGUI = RevivalGUI(plugin, manager)

    val deathListener = listen<PlayerDeathEvent> {
        val player = it.entity
        val killer = player.killer

        if (killer is Player) {
            manager.addHeart(killer)
        }

        manager.removeHeart(player)
    }

    val clickListener = listen<PlayerInteractEvent> {
        val player = it.player

        if (it.action != Action.RIGHT_CLICK_AIR && it.action != Action.RIGHT_CLICK_BLOCK) return@listen

        val item = it.item ?: return@listen

        // Handle Heart item
        if (item.type == Material.RED_DYE) {
            val meta = item.itemMeta ?: return@listen
            val container = meta.persistentDataContainer

            val heartKey = NamespacedKey(plugin, "heart")

            if (container.has(heartKey, PersistentDataType.BYTE)) {
                manager.addHeart(player)
                player.inventory.remove(item)
                return@listen
            }
        }

        // Handle Revival Beacon item
        if (item.type == Material.BEACON) {
            val meta = item.itemMeta ?: return@listen
            val container = meta.persistentDataContainer

            val revivalBeaconKey = NamespacedKey(plugin, "revival_beacon")

            if (container.has(revivalBeaconKey, PersistentDataType.BYTE)) {
                revivalGUI.openRevivalGUI(player)
                it.isCancelled = true
                return@listen
            }
        }
        
        return@listen
    }

    fun enableAll() {
        deathListener.register()
        clickListener.register()
    }

    fun disableAll() {
        deathListener.unregister()
        clickListener.unregister()
    }
}