package com.github.breadbyte.itemcustomizer.server.commands.impl.model.tint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.tint.IModelTintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.tint.ModelTintParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;

import java.util.List;

public class ModelTintOperations implements IModelTintOperations {
    @Override
    public Result<String> apply(ModelTintParams params) {
        var index = params.tintIndex();
        var color = params.tintColor();
        var playerItem = params.item();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var dyemap = itemComps.get(DataComponentTypes.CUSTOM_MODEL_DATA);

        // If the data doesn't exist at all, create it
        String value = "Set color index " + index + " to #" + String.format("%06X", (0xFFFFFF & color)) + " for held item";
        if (dyemap == null) {
            List<Integer> intList = new java.util.ArrayList<>();
            // populate until specified index
            for (int i = 0; i <= index; i++)
                intList.add(-1);
            intList.set(index, color);

            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(), intList));
            return Result.ok(value);
        }

        // If the data exists but the index is out of bounds, expand it
        if (index >= dyemap.colors().size()) {
            // Deep copy the entire color list
            var newCol = new java.util.ArrayList<>(dyemap.colors());

            if (newCol.size() <= index) {
                // populate until specified index
                for (int i = newCol.size(); i <= index; i++)
                    newCol.add(-1);
            }

            newCol.set(index, color);

            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(), newCol));
            return Result.ok(value);
        }

        // Otherwise, modify existing data
        // Copy existing colors
        var newCol = new java.util.ArrayList<>(dyemap.colors());
        newCol.set(index, color);

        // Write back
        playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(dyemap.floats(), dyemap.flags(), dyemap.strings(), List.copyOf(newCol)));
        return Result.ok(value);
    }

    @Override
    public Result<String> reset(ModelTintParams params) {
        var playerItem = params.item();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var dyemap = itemComps.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        // Replace color list with empty list, but keep the rest of the data
        playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                dyemap == null ? List.of() : dyemap.floats(),
                dyemap == null ? List.of() : dyemap.flags(),
                dyemap == null ? List.of() : dyemap.strings(),
                List.of()));
        return Result.ok("Tints cleared for held item");
    }
}
