package site.thatkid.lifesteal.structures

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import site.thatkid.lifesteal.managers.Manager

class RevivalBeacon(private val plugin: JavaPlugin, private val manager: Manager) {

    /**
     * Check if the blocks around a beacon form a valid revival beacon structure
     * Structure: Beacon in center, 4 diamond blocks on sides, 4 netherite ingots in corners
     */
    fun isValidRevivalStructure(beaconBlock: Block): Boolean {
        val location = beaconBlock.location
        
        // Check if center is a beacon
        if (beaconBlock.type != Material.BEACON) return false
        
        // Define relative positions for the structure
        val diamondPositions = listOf(
            location.clone().add(1.0, 0.0, 0.0),  // East
            location.clone().add(-1.0, 0.0, 0.0), // West  
            location.clone().add(0.0, 0.0, 1.0),  // South
            location.clone().add(0.0, 0.0, -1.0)  // North
        )
        
        val netheritePositions = listOf(
            location.clone().add(1.0, 0.0, 1.0),   // Southeast
            location.clone().add(-1.0, 0.0, 1.0),  // Southwest
            location.clone().add(1.0, 0.0, -1.0),  // Northeast
            location.clone().add(-1.0, 0.0, -1.0)  // Northwest
        )
        
        // Check diamond blocks
        for (pos in diamondPositions) {
            val block = pos.world?.getBlockAt(pos)
            if (block?.type != Material.DIAMOND_BLOCK) return false
        }
        
        // Check netherite ingot blocks
        for (pos in netheritePositions) {
            val block = pos.world?.getBlockAt(pos)
            if (block?.type != Material.NETHERITE_BLOCK) return false
        }
        
        return true
    }
    
    /**
     * Activate the revival beacon and consume the structure
     */
    fun activateRevivalBeacon(player: Player, beaconBlock: Block): Boolean {
        if (!isValidRevivalStructure(beaconBlock)) {
            player.sendMessage("§cInvalid revival beacon structure!")
            return false
        }
        
        // Show available players to revive
        val bannedPlayers = manager.getBannedPlayersFromHeartLoss()
        
        if (bannedPlayers.isEmpty()) {
            player.sendMessage("§eNo players are currently banned from heart loss!")
            return false
        }
        
        // Set a flag that this player can use the revive command
        player.persistentDataContainer.set(
            org.bukkit.NamespacedKey(plugin, "can_revive"), 
            org.bukkit.persistence.PersistentDataType.BYTE, 
            1
        )
        
        // Store beacon location for later consumption
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