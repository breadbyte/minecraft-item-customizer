package com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.IModelCopyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.ModelCopyParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;

public class ModelCopyOperations implements IModelCopyOperations {
    @Override
    public Result<String> copyAll(ModelCopyParams params) {
        var mainHand = params.mainHand();
        var offHand = params.offHand();

        mainHand.applyComponentsFrom(offHand.getComponents());

        return Result.ok();
    }

    @Override
    public Result<String> copyName(ModelCopyParams params) {
        var mainHand = params.mainHand();
        var offHand = params.offHand();

        var name = offHand.get(DataComponentTypes.CUSTOM_NAME);
        if (name != null) {
            mainHand.set(DataComponentTypes.CUSTOM_NAME, name);
        } else {
            mainHand.remove(DataComponentTypes.CUSTOM_NAME);
        }

        return Result.ok();
    }

    @Override
    public Result<String> copyLore(ModelCopyParams params) {
        var mainHand = params.mainHand();
        var offHand = params.offHand();

        var lore = offHand.get(DataComponentTypes.LORE);
        if (lore != null) {
            mainHand.set(DataComponentTypes.LORE, lore);
        } else {
            mainHand.remove(DataComponentTypes.LORE);
        }

        return Result.ok();
    }

    @Override
    public Result<String> copyModel(ModelCopyParams params) {
        var mainHand = params.mainHand();
        var offHand = params.offHand();
        var offHandComps = offHand.getComponents();

        // Model (Apply)
        var model = offHandComps.get(DataComponentTypes.ITEM_MODEL);
        if (model != null) {
            mainHand.set(DataComponentTypes.ITEM_MODEL, model);
        } else {
            mainHand.remove(DataComponentTypes.ITEM_MODEL);
        }

        // Equippable (Equipment)
        var equippable = offHandComps.get(DataComponentTypes.EQUIPPABLE);
        if (equippable != null) {
            mainHand.set(DataComponentTypes.EQUIPPABLE, equippable);
        } else {
            mainHand.remove(DataComponentTypes.EQUIPPABLE);
        }

        // Glint
        var glint = offHandComps.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        if (glint != null) {
            mainHand.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        } else {
            mainHand.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        }

        // Tint
        var tint = offHandComps.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (tint != null) {
            mainHand.set(DataComponentTypes.CUSTOM_MODEL_DATA, tint);
        } else {
            mainHand.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
        }

        // Dye
        var dye = offHandComps.get(DataComponentTypes.DYED_COLOR);
        if (dye != null) {
            mainHand.set(DataComponentTypes.DYED_COLOR, dye);
        } else {
            mainHand.remove(DataComponentTypes.DYED_COLOR);
        }

        return Result.ok();
    }
}
