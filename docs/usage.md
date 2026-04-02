# Usage

## Commands

| Command                       | Alias | Description                                            | Permission              |
| ----------------------------- | ----- | ------------------------------------------------------ | ----------------------- |
| `/structloc <structure_id>`   | `/sl` | Select a structure to locate with your compass         | `structloc.use`         |
| `/structloc reload`           | -     | Reload the plugin configuration                        | `structloc.command.reload` |
| `/givestructloc [player]`     | -     | Give a Structure Locator compass to a player           | `structloc.command.give` |

## Permissions

| Permission                | Default | Description                                                  |
| ------------------------- | ------- | ------------------------------------------------------------ |
| `structloc.use`           | True    | Allows players to use `/structloc` and right-click compass   |
| `structloc.command.reload` | OP     | Allows players to reload the plugin configuration           |
| `structloc.command.give`  | OP     | Allows players to give the compass with `/givestructloc`    |
| `structloc.craft`         | True    | Allows players to craft the structure finder compass         |

## Crafting

Players with the `structloc.craft` permission can craft a Structure Locator compass using the following recipe:

```
R I R
I D I
R I R
```

Where:
- **R** = Redstone Block
- **I** = Iron Block  
- **D** = Diamond Block

## Configuration

The plugin can be configured via `plugins/StructLoc/config.yml`:

```yaml
search:
  max-radius: 20000              # Maximum search distance in blocks
  find-unexplored: false         # Also search for unexplored structures

compass:
  update-interval-ticks: 5       # Compass update frequency (20 ticks = 1 second)
  name: "Structure Locator"      # Compass display name
  use-animations: true           # Use animations for compass needle
  cooldown-seconds: 5            # Cooldown between compass updates
  proximity-distance-blocks: 20  # Auto-reset distance
  particle-trail-enabled: true   # Show particle trail to structure
```
