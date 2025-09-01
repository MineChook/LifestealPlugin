package site.thatkid.lifesteal.recipes

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.ConfigurationSection
import site.thatkid.lifesteal.items.Heart
import site.thatkid.lifesteal.items.RevivalBeaconItem

class CraftingRecipes(private val plugin: JavaPlugin) {

    fun registerHeartRecipe() {
        val heartItem = Heart(plugin).createItem()
        val heartKey = NamespacedKey(plugin, "heart_recipe")
        
        // Get recipe configuration from config.yml
        val config = plugin.config
        val recipesSection = config.getConfigurationSection("recipes")
        val heartRecipeSection = recipesSection?.getConfigurationSection("heart")
        
        if (heartRecipeSection == null) {
            plugin.logger.warning("Heart recipe configuration not found, using default recipe")
            registerDefaultHeartRecipe(heartItem, heartKey)
            return
        }
        
        try {
            val shape = heartRecipeSection.getStringList("shape")
            val ingredients = heartRecipeSection.getConfigurationSection("ingredients")
            
            if (shape.isEmpty() || ingredients == null) {
                plugin.logger.warning("Invalid heart recipe configuration, using default recipe")
                registerDefaultHeartRecipe(heartItem, heartKey)
                return
            }
            
            val heartRecipe = ShapedRecipe(heartKey, heartItem).apply {
                shape(*shape.toTypedArray())
                
                // Set ingredients based on configuration
                for (key in ingredients.getKeys(false)) {
                    val materialName = ingredients.getString(key)
                    if (materialName != null) {
                        val material = Material.getMaterial(materialName)
                        if (material != null) {
                            setIngredient(key.first(), material)
                        } else {
                            plugin.logger.warning("Unknown material '$materialName' in heart recipe for ingredient '$key'")
                        }
                    }
                }
            }
            
            Bukkit.addRecipe(heartRecipe)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to register heart recipe from config: ${e.message}, using default recipe")
            registerDefaultHeartRecipe(heartItem, heartKey)
        }
    }
    
    private fun registerDefaultHeartRecipe(heartItem: org.bukkit.inventory.ItemStack, heartKey: NamespacedKey) {
        val heartRecipe = ShapedRecipe(heartKey, heartItem).apply {
            shape("DND", "NHN", "DND")
            setIngredient('D', Material.DIAMOND_BLOCK)
            setIngredient('N', Material.NETHERITE_INGOT)
            setIngredient('H', Material.NETHER_STAR)
        }
        
        try {
            Bukkit.addRecipe(heartRecipe)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to register default heart recipe: ${e.message}")
        }
    }
    
    fun registerRevivalBeaconRecipe() {
        val revivalBeaconItem = RevivalBeaconItem(plugin).createItem()
        val revivalBeaconKey = NamespacedKey(plugin, "revival_beacon_recipe")
        
        // Get recipe configuration from config.yml
        val config = plugin.config
        val recipesSection = config.getConfigurationSection("recipes")
        val revivalBeaconRecipeSection = recipesSection?.getConfigurationSection("revival_beacon")
        
        if (revivalBeaconRecipeSection == null) {
            plugin.logger.warning("Revival beacon recipe configuration not found, using default recipe")
            registerDefaultRevivalBeaconRecipe(revivalBeaconItem, revivalBeaconKey)
            return
        }
        
        try {
            val shape = revivalBeaconRecipeSection.getStringList("shape")
            val ingredients = revivalBeaconRecipeSection.getConfigurationSection("ingredients")
            
            if (shape.isEmpty() || ingredients == null) {
                plugin.logger.warning("Invalid revival beacon recipe configuration, using default recipe")
                registerDefaultRevivalBeaconRecipe(revivalBeaconItem, revivalBeaconKey)
                return
            }
            
            val revivalBeaconRecipe = ShapedRecipe(revivalBeaconKey, revivalBeaconItem).apply {
                shape(*shape.toTypedArray())
                
                // Set ingredients based on configuration
                for (key in ingredients.getKeys(false)) {
                    val materialName = ingredients.getString(key)
                    if (materialName != null) {
                        if (materialName == "HEART") {
                            // Special case for heart items
                            setIngredient(key.first(), Heart(plugin).createItem())
                        } else {
                            val material = Material.getMaterial(materialName)
                            if (material != null) {
                                setIngredient(key.first(), material)
                            } else {
                                plugin.logger.warning("Unknown material '$materialName' in revival beacon recipe for ingredient '$key'")
                            }
                        }
                    }
                }
            }
            
            Bukkit.addRecipe(revivalBeaconRecipe)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to register revival beacon recipe from config: ${e.message}, using default recipe")
            registerDefaultRevivalBeaconRecipe(revivalBeaconItem, revivalBeaconKey)
        }
    }
    
    private fun registerDefaultRevivalBeaconRecipe(revivalBeaconItem: org.bukkit.inventory.ItemStack, revivalBeaconKey: NamespacedKey) {
        val revivalBeaconRecipe = ShapedRecipe(revivalBeaconKey, revivalBeaconItem).apply {
            shape("HNH", "NBN", "HNH")
            setIngredient('H', Heart(plugin).createItem())
            setIngredient('N', Material.NETHERITE_INGOT)
            setIngredient('B', Material.BEACON)
        }
        
        try {
            Bukkit.addRecipe(revivalBeaconRecipe)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to register default revival beacon recipe: ${e.message}")
        }
    }
    
    fun unregisterRecipes() {
        try {
            Bukkit.removeRecipe(NamespacedKey(plugin, "heart_recipe"))
            Bukkit.removeRecipe(NamespacedKey(plugin, "revival_beacon_recipe"))
        } catch (e: Exception) {
            plugin.logger.warning("Failed to unregister recipes: ${e.message}")
        }
    }
}