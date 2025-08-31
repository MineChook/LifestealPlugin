package site.thatkid.lifesteal.managers

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.items.Heart
import java.util.Date

class Manager(private val plugin: JavaPlugin, private val config: Configuration) {

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
        player.ban("You have been banned for losing all your hearts.", expires, null, true)
    }

}