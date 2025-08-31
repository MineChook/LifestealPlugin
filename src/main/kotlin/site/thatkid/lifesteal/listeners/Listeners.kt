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
import site.thatkid.lifesteal.managers.Manager

class Listeners(private val plugin: JavaPlugin, private val manager: Manager) {

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
        if (item.type != Material.RED_DYE) return@listen

        val meta = item.itemMeta ?: return@listen
        val container = meta.persistentDataContainer

        val key = NamespacedKey(plugin, "heart")

        if (container.has(key, PersistentDataType.BYTE)) {
            manager.addHeart(player)
            player.inventory.remove(item)
            return@listen
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