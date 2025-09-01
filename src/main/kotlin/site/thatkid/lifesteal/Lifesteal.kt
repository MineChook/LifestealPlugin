package site.thatkid.lifesteal

import net.axay.kspigot.main.KSpigot
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import site.thatkid.lifesteal.gui.Items
import site.thatkid.lifesteal.listeners.Listeners
import site.thatkid.lifesteal.managers.Manager
import site.thatkid.lifesteal.recipes.CraftingRecipes

class Lifesteal : KSpigot() {

    lateinit var pluginConfig: FileConfiguration

    lateinit var manager : Manager
    lateinit var listeners: Listeners
    lateinit var craftingRecipes: CraftingRecipes
    lateinit var items: Items

    override fun startup() {
        pluginConfig = this.config
        manager = Manager(this, pluginConfig)
        listeners = Listeners(this, manager)
        craftingRecipes = CraftingRecipes(this)
        items = Items(this)
        
        listeners.enableAll()
        craftingRecipes.registerHeartRecipe()
        craftingRecipes.registerRevivalBeaconRecipe()

        manager.load()
        
        // Set configuration defaults
        pluginConfig.addDefault("maxHealth", 40.0)
        
        // Add recipe defaults
        pluginConfig.addDefault("recipes.heart.shape", listOf("DND", "NHN", "DND"))
        pluginConfig.addDefault("recipes.heart.ingredients.D", "DIAMOND_BLOCK")
        pluginConfig.addDefault("recipes.heart.ingredients.N", "NETHERITE_INGOT")
        pluginConfig.addDefault("recipes.heart.ingredients.H", "NETHER_STAR")
        
        pluginConfig.addDefault("recipes.revival_beacon.shape", listOf("HNH", "NBN", "HNH"))
        pluginConfig.addDefault("recipes.revival_beacon.ingredients.H", "HEART")
        pluginConfig.addDefault("recipes.revival_beacon.ingredients.N", "NETHERITE_INGOT")
        pluginConfig.addDefault("recipes.revival_beacon.ingredients.B", "BEACON")
        
        pluginConfig.options().copyDefaults(true)

        saveResource("config.yml", false)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name == "lifesteal") {
            if (sender.hasPermission("lifesteal")) {
                if (args.isEmpty()) {
                    sender.sendMessage("Lifesteal v1.0 by ThatKid")
                    return true
                }
                if (args[0] == "reload") {
                    reload()
                    sender.sendMessage("Lifesteal reloaded!")
                    return true
                }
            }
        }
        if (command.name == "drain") {
            if (sender !is Player) {
                sender.sendMessage("Only players can use this command.")
                return true
            }
            manager.drainHeart(sender)
        }
        return false
    }

    private fun reload() {
        listeners.disableAll()
        craftingRecipes.unregisterRecipes()
        manager.save()

        // Reload configuration from file
        reloadConfig()
        pluginConfig = this.config
        manager = Manager(this, pluginConfig)
        listeners = Listeners(this, manager)
        craftingRecipes = CraftingRecipes(this)
        
        listeners.enableAll()
        craftingRecipes.registerHeartRecipe()
        craftingRecipes.registerRevivalBeaconRecipe()

        manager.load()
    }

    override fun shutdown() {
        listeners.disableAll()
        craftingRecipes.unregisterRecipes()
        manager.save()
    }
}
