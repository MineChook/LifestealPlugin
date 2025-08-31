package site.thatkid.lifesteal.recipes

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.items.Heart

class CraftingRecipes(private val plugin: JavaPlugin) {

    fun registerHeartRecipe() {
        val heartItem = Heart(plugin).createItem()
        val heartKey = NamespacedKey(plugin, "heart_recipe")
        
        val heartRecipe = ShapedRecipe(heartKey, heartItem).apply {
            shape("DND", "NHN", "DND")
            setIngredient('D', Material.DIAMOND)
            setIngredient('N', Material.NETHERITE_INGOT)
            setIngredient('H', Material.NETHER_STAR)
        }
        
        try {
            Bukkit.addRecipe(heartRecipe)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to register heart recipe: ${e.message}")
        }
    }
    
    fun unregisterRecipes() {
        try {
            Bukkit.removeRecipe(NamespacedKey(plugin, "heart_recipe"))
        } catch (e: Exception) {
            plugin.logger.warning("Failed to unregister heart recipe: ${e.message}")
        }
    }
}