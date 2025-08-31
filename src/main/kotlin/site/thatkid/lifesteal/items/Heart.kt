package site.thatkid.lifesteal.items

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class Heart(private val plugin: JavaPlugin) {

    val key: NamespacedKey = NamespacedKey(plugin, "heart")

    fun createItem(): ItemStack {
        val item = ItemStack(Material.RED_DYE)
        val itemMeta = item.itemMeta!!

        itemMeta.setDisplayName("§cHeart")
        itemMeta.persistentDataContainer.set(key, PersistentDataType.BYTE, 1)

        item.itemMeta = itemMeta

        return item
    }
}