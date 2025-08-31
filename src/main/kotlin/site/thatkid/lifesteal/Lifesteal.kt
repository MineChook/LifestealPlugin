package site.thatkid.lifesteal

import net.axay.kspigot.main.KSpigot
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import site.thatkid.lifesteal.listeners.Listeners
import site.thatkid.lifesteal.managers.Manager
import site.thatkid.lifesteal.recipes.CraftingRecipes

class Lifesteal : KSpigot() {

    lateinit var pluginConfig: FileConfiguration

    lateinit var manager : Manager
    lateinit var listeners: Listeners
    lateinit var craftingRecipes: CraftingRecipes

    override fun startup() {
        pluginConfig = this.getConfig()
        manager = Manager(this, pluginConfig)
        listeners = Listeners(this, manager)
        craftingRecipes = CraftingRecipes(this)
        
        listeners.enableAll()
        craftingRecipes.registerHeartRecipe()
        craftingRecipes.registerRevivalBeaconRecipe()
        
        pluginConfig.addDefault("maxHealth", 40.0)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String?>?): Boolean {
        if (command.name == "lifesteal") {
            if (sender.hasPermission("lifesteal")) {
                if (args == null || args.isEmpty()) {
                    sender.sendMessage("Lifesteal v1.0 by thatkid.site")
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

        pluginConfig = this.getConfig()
        manager = Manager(this, pluginConfig)
        listeners = Listeners(this, manager)
        craftingRecipes = CraftingRecipes(this)
        
        listeners.enableAll()
        craftingRecipes.registerHeartRecipe()
        craftingRecipes.registerRevivalBeaconRecipe()
    }

    override fun shutdown() {
        listeners.disableAll()
        craftingRecipes.unregisterRecipes()
    }
}
