# Revival Beacon and Heart Crafting Implementation

## Overview
This implementation adds two major features to the LifestealPlugin:

1. **Revival Beacon Item System** - A craftable beacon item that opens a GUI to revive players who died from heart loss
2. **Heart Crafting Recipe** - A crafting recipe for heart items using expensive materials

## Revival Beacon System

### Crafting Recipe
The Revival Beacon is a craftable item with an expensive 3x3 recipe:
```
NDB
DBD  (N = Netherite Ingot, D = Diamond Block, B = Beacon)
BDN
```

### Materials Required
- 4x Netherite Ingots
- 4x Diamond Blocks  
- 1x Beacon

### Usage
1. Craft a Revival Beacon using the recipe above
2. Right-click the Revival Beacon item to open the Revival GUI
3. Click on a player head to select which player to revive
4. The Revival Beacon item is consumed upon successful revival

### Key Features
- **GUI Interface**: Opens a user-friendly GUI showing player heads of banned players
- **Only revives heart-loss deaths**: Players who died from other causes cannot be revived
- **Item-based system**: No multiblock structures required - just craft and use
- **No permissions required**: Available to all players by default
- **Item consumption**: The expensive Revival Beacon item is consumed when used
- **Death cause tracking**: Distinguishes between heart-loss bans and other deaths

### GUI Features
- Shows player heads with names for easy identification
- Clear warning that the item will be consumed
- Close button to cancel the revival process
- Handles up to 28 banned players in the interface

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
1. **RevivalBeaconItem** (`items/RevivalBeaconItem.kt`) - Creates the craftable Revival Beacon item
2. **RevivalGUI** (`gui/RevivalGUI.kt`) - Handles the GUI interface for player selection using KSpigot
3. **CraftingRecipes** (`recipes/CraftingRecipes.kt`) - Manages heart and revival beacon crafting recipes

### Modified Classes
1. **Manager** - Added death cause tracking and simplified revival functionality (removed beacon activation logic)
2. **Listeners** - Updated to handle Revival Beacon item clicks instead of multiblock interactions
3. **Lifesteal** - Removed `/revive` command, added revival beacon recipe registration
4. **plugin.yml** - Removed revive command

### Removed Classes
1. **RevivalBeacon** (`structures/RevivalBeacon.kt`) - No longer needed with item-based system

### Death Tracking System
The plugin tracks which players were banned specifically from heart loss vs other causes. Only players in the "heart loss banned" list can be revived using the Revival Beacon item.

### GUI System
Uses KSpigot's GUI system to create an interactive interface:
- 6x9 inventory GUI with black glass pane borders
- Player heads showing all banned players
- Click to select and revive
- Automatic item consumption on successful revival

## Commands
- `/drain` - Existing command to drain hearts for items  
- `/lifesteal [reload]` - Existing plugin management command

**Removed Commands:**
- `/revive` - Replaced with GUI interaction system

## Configuration
The plugin uses the existing configuration system and doesn't require additional config entries for the new features.

## Usage Examples

### Crafting a Revival Beacon
1. Open a crafting table
2. Place materials in pattern: Netherite Ingots in corners, Diamond Blocks on sides, Beacon in center
3. Craft to receive a Revival Beacon item

### Using the Revival Beacon
1. Hold the Revival Beacon item in your hand
2. Right-click to open the Revival GUI
3. Click on a player head to revive that player
4. The Revival Beacon item will be consumed

### Crafting Hearts
1. Open a crafting table
2. Place materials in the cross pattern: diamonds in corners, netherite ingots on sides, nether star in center
3. Craft to receive a heart item
4. Right-click the heart item to restore health

This implementation provides a more intuitive and user-friendly system for revival while maintaining the expensive cost and balanced gameplay. The GUI interface makes it clear which players can be revived and provides immediate feedback on the action.