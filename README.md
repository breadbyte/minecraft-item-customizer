# Minecraft Item Customizer

## How to use
##### Only works server-side!
##### All operations require 1 xp, excluding the `reset` functions.
##### All operations operate on the currently held item.

## Item Model
Permission node `itemcustomizer.customize`

- `/model apply <namespace> <path>` applies an item model to the currently held item. For example, `/model apply minecraft stone` will turn whatever you have in your hand into `minecraft:stone`. Only visually in the inventory. Any items and blocks will still keep their properties.

- `/model reset` reverts items back to their previous item model, if their item model has changed.

These commands also apply to custom entity models, i.e. custom elytras/tridents/etc.
## Item Name
Permission node `itemcustomizer.rename`
- `/rename <name>` renames the currently held item.

- `/rename reset` resets the name of the currently held item.

- `/rename help` returns a link to a website to help create custom names.
## Item Lore
Permission node `itemcustomizer.lore`
- `/lore text` adds a line of lore to the currently held item.

- `/lore reset` resets all lines of lores from the currently held item.

- `/lore help` returns a link to a website to help create custom names.

## How to build
`gradle jar` then `gradle remapJar`, then the output jar ends up in `build/libs`
