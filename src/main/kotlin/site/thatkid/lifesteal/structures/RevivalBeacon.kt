package site.thatkid.lifesteal.structures

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.managers.Manager

class RevivalBeacon(private val plugin: JavaPlugin, private val manager: Manager) {

    fun isValidRevivalStructure(beaconBlock: Block): Boolean {
        val location = beaconBlock.location
        
        if (beaconBlock.type != Material.BEACON) return false
        
        val diamondPositions = listOf(
            location.clone().add(1.0, 0.0, 0.0),
            location.clone().add(-1.0, 0.0, 0.0),
            location.clone().add(0.0, 0.0, 1.0),
            location.clone().add(0.0, 0.0, -1.0)
        )
        
        val netheritePositions = listOf(
            location.clone().add(1.0, 0.0, 1.0),
            location.clone().add(-1.0, 0.0, 1.0),
            location.clone().add(1.0, 0.0, -1.0),
            location.clone().add(-1.0, 0.0, -1.0)
        )
        
        for (pos in diamondPositions) {
            val block = pos.world?.getBlockAt(pos)
            if (block?.type != Material.DIAMOND_BLOCK) return false
        }
        
        for (pos in netheritePositions) {
            val block = pos.world?.getBlockAt(pos)
            if (block?.type != Material.NETHERITE_BLOCK) return false
        }
        
        return true
    }
    
    fun activateRevivalBeacon(player: Player, beaconBlock: Block): Boolean {
        if (!isValidRevivalStructure(beaconBlock)) {
            player.sendMessage("§cInvalid revival beacon structure!")
            return false
        }
        
        val bannedPlayers = manager.getBannedPlayersFromHeartLoss()
        
        if (bannedPlayers.isEmpty()) {
            player.sendMessage("§eNo players are currently banned from heart loss!")
            return false
        }
        
        player.persistentDataContainer.set(
            org.bukkit.NamespacedKey(plugin, "can_revive"), 
            org.bukkit.persistence.PersistentDataType.BYTE, 
            1
        )
        
        val loc = beaconBlock.location
        player.persistentDataContainer.set(
            org.bukkit.NamespacedKey(plugin, "beacon_x"),
            org.bukkit.persistence.PersistentDataType.INTEGER,
            loc.blockX
        )
        player.persistentDataContainer.set(
            org.bukkit.NamespacedKey(plugin, "beacon_y"),
            org.bukkit.persistence.PersistentDataType.INTEGER,
            loc.blockY
        )
        player.persistentDataContainer.set(
            org.bukkit.NamespacedKey(plugin, "beacon_z"),
            org.bukkit.persistence.PersistentDataType.INTEGER,
            loc.blockZ
        )
        
        player.sendMessage("§aRevival beacon activated! Use /revive <playername> to revive a player.")
        player.sendMessage("§eBanned players: ${bannedPlayers.joinToString(", ")}")
        
        return true
    }
}