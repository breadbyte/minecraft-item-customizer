# Minecraft Item Customizer

## How to use
##### Works server-side and client-side (singleplayer only)
##### All operations require 1 xp, except the `reset` functions.
##### All operations run on the currently held item.

## Item Model
Permission node `itemcustomizer.customize`

** _These commands also apply to custom entity models, i.e. custom elytras/tridents/etc._ **

###### Custom Models

- `/model apply <namespace> <path>` applies an item model to the currently held item. For example, `/model apply minecraft stone` will turn whatever you have in your hand into `minecraft:stone`. Only visually in the inventory. Any items and blocks will still keep their properties.

- `/model reset` reverts items back to their previous item model, if their item model has changed.

###### Glint

- `/model glint add` adds the enchanted glint effect to the currently held item.

- `/model glint remove` removes the enchanted glint effect from the currently held item.

###### Dyes

- `/model dye <color>` dyes the currently held item from a specified RGB mix. A calculator for this can be found on the [Minecraft Wiki (Data component format/dyed color)](https://minecraft.wiki/w/Data_component_format/dyed_color).

- `/model dye remove` resets the dyed color of the currently held item.

## Item Name
Permission node `itemcustomizer.rename`
- `/rename <name>` renames the currently held item. It accepts tellraw format.

- `/rename reset` resets the name of the currently held item.

- `/rename help` returns a link to a website to help create custom names. You can either use [birdflop](https://www.birdflop.com/resources/rgb/), [colorize.fun](https://colorize.fun/en/minecraft) to create custom names.
## Item Lore
Permission node `itemcustomizer.lore`
- `/lore text` adds a line of lore to the currently held item. It accepts tellraw format.

- `/lore reset` resets all lines of lores from the currently held item.

- `/lore help` returns a link to a website to help create custom names.

## How to build
`gradle jar` then `gradle remapJar`, then the output jar ends up in `build/libs`
