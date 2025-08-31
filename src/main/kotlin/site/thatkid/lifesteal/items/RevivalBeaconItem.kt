package site.thatkid.lifesteal.items

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class RevivalBeaconItem(private val plugin: JavaPlugin) {

    val key: NamespacedKey = NamespacedKey(plugin, "revival_beacon")

    fun createItem(): ItemStack {
        val item = ItemStack(Material.BEACON)
        val itemMeta = item.itemMeta!!

        itemMeta.setDisplayName("§6Revival Beacon")
        itemMeta.lore = listOf(
            "§7Right-click to open the revival menu",
            "§7and revive players banned from heart loss",
            "§c§lConsumes the item on use"
        )
        itemMeta.persistentDataContainer.set(key, PersistentDataType.BYTE, 1)

        item.itemMeta = itemMeta

        return item
    }
}