# Revival Beacon and Heart Crafting Implementation

## Overview
This implementation adds two major features to the LifestealPlugin:

1. **Revival Beacon System** - A multiblock structure that can revive players who died from heart loss
2. **Heart Crafting Recipe** - A crafting recipe for heart items using expensive materials

## Revival Beacon System

### Structure Requirements
The revival beacon requires a 3x3 pattern with the beacon in the center:
- **Center**: 1x Beacon
- **Sides** (4 blocks): Diamond Blocks (North, South, East, West of beacon)
- **Corners** (4 blocks): Netherite Blocks (Northeast, Northwest, Southeast, Southwest of beacon)

```
NBD
DBD  (N = Netherite Block, D = Diamond Block, B = Beacon)
DBD
```

### Usage
1. Place the revival beacon structure as described above
2. Right-click the beacon to activate it
3. The plugin will show you which players can be revived (those banned from heart loss)
4. Use `/revive <playername>` to revive a specific player
5. The entire structure is consumed upon successful revival

### Key Features
- **Only revives heart-loss deaths**: Players who died from other causes cannot be revived
- **Structure validation**: Checks that the beacon structure is built correctly
- **Permission system**: Requires `lifesteal.revive` permission
- **Structure consumption**: The expensive materials are consumed when used
- **Death cause tracking**: Distinguishes between heart-loss bans and other deaths

## Heart Crafting Recipe

### Recipe Pattern
The heart crafting recipe uses a cross pattern:
```
DND
NHN  (D = Diamond, N = Netherite Ingot, H = Nether Star)
DND
```

This creates one Heart item that can be consumed to restore health.

### Materials Required
- 4x Diamond
- 4x Netherite Ingot  
- 1x Nether Star

This makes hearts expensive to craft, maintaining game balance in lifesteal scenarios.

## Technical Implementation

### New Classes Added
1. **RevivalBeacon** (`structures/RevivalBeacon.kt`) - Handles beacon structure detection and activation
2. **CraftingRecipes** (`recipes/CraftingRecipes.kt`) - Manages heart crafting recipe registration

### Modified Classes
1. **Manager** - Added death cause tracking and revival functionality
2. **Listeners** - Added beacon interaction handling
3. **Lifesteal** - Added `/revive` command and recipe initialization
4. **plugin.yml** - Added new command and permissions

### Death Tracking System
The plugin now tracks which players were banned specifically from heart loss vs other causes. Only players in the "heart loss banned" list can be revived using the revival beacon.

### Permissions
- `lifesteal.revive` - Required to use revival beacons (default: op)
- `lifesteal` - General plugin permissions (default: op)

## Commands
- `/revive <playername>` - Revive a player who died from heart loss (requires active beacon)
- `/drain` - Existing command to drain hearts for items
- `/lifesteal [reload]` - Existing plugin management command

## Configuration
The plugin uses the existing configuration system and doesn't require additional config entries for the new features.

## Usage Examples

### Setting up a Revival Beacon
1. Place a beacon
2. Place diamond blocks on the 4 cardinal directions adjacent to the beacon
3. Place netherite blocks on the 4 diagonal corners adjacent to the beacon
4. Right-click the beacon to activate it

### Crafting Hearts
1. Open a crafting table
2. Place materials in the cross pattern: diamonds in corners, netherite ingots on sides, nether star in center
3. Craft to receive a heart item
4. Right-click the heart item to restore health

This implementation provides a balanced and expensive way to both craft hearts and revive players, maintaining the challenging nature of lifesteal gameplay while providing recovery options for dedicated players.