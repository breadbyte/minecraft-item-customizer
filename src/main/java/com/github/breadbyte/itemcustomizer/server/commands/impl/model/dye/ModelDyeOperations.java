package com.github.breadbyte.itemcustomizer.server.commands.impl.model.dye;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye.IModelDyeOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye.ModelDyeParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;

public class ModelDyeOperations implements IModelDyeOperations {

    @Override
    public Result<String> apply(ModelDyeParams params) {
        var item = params.item();
        var color = params.color();

        // Get the default dyed color for the item
        var defaultDyedColor = item.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);
        item.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color.
        item.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color));

        return Result.ok();
    }

    @Override
    public Result<String> reset(ModelDyeParams params) {
        var item = params.item();

        item.remove(DataComponentTypes.DYED_COLOR);

        // Get the default dyed color for the item
        var defaultDyedColor = item.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);

        // Remove the current dyed color component
        item.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color
        item.set(DataComponentTypes.DYED_COLOR, defaultDyedColor);

        return Result.ok();
    }
}
