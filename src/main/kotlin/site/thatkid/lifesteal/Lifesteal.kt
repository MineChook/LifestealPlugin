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
        if (command.name == "revive") {
            if (sender !is Player) {
                sender.sendMessage("Only players can use this command.")
                return true
            }
            if (args == null || args.isEmpty()) {
                sender.sendMessage("§eUsage: /revive <playername>")
                return true
            }
            val targetName = args[0] ?: return true
            val success = manager.revivePlayer(sender, targetName)
            if (success) {
                sender.sendMessage("§aSuccessfully revived player $targetName!")
                sender.sendMessage("§eThe revival beacon has been consumed.")
            } else {
                sender.sendMessage("§cFailed to revive player $targetName. They may not be banned from heart loss, or you need to activate a revival beacon first.")
            }
            return true
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
    }

    override fun shutdown() {
        listeners.disableAll()
        craftingRecipes.unregisterRecipes()
    }
}
