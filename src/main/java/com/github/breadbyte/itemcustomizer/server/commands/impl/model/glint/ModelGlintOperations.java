package com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.IModelGlintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.ModelGlintParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;

public class ModelGlintOperations implements IModelGlintOperations {
    @Override
    public Result<String> toggle(ModelGlintParams params) {
        var playerItem = params.item();

        // Refer to table below for logic
        // - HAS OVERRIDE? FLIP THE FLAG, EXIT EARLY
        // - IF ENCHANTED
        //  - SET OVERRIDE TO FALSE (DISABLE GLINT)
        // - IF NOT ENCHANTED
        //  - SET OVERRIDE TO TRUE (ENABLE GLINT)
        var override = playerItem.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        if (override != null) {
            if (override)
                playerItem.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
            else
                playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            return Result.ok();
        }

        if (playerItem.hasEnchantments()) {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            return Result.ok();
        } else {
            playerItem.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
            return Result.ok();
        }
    }
}
