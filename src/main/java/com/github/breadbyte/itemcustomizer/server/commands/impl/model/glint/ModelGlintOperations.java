package com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.IModelGlintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.ModelGlintParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;

public class ModelGlintOperations implements IModelGlintOperations {
    @Override
    public Result<String> toggle(ModelGlintParams params) {
        var playerItem = params.item();
        var override = playerItem.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);

        // Determine the new state for the glint override
        var newOverrideState = determineGlintOverrideState(override, playerItem.hasEnchantments());

        // Apply the new state
        if (newOverrideState == null) {
            playerItem.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        } else {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, newOverrideState);
        }

        return Result.ok();
    }

    /**
     * Determines the new state for the glint override based on the current override and enchantment status.
     *
     * @param currentOverride The current override value, or null if not set.
     * @param hasEnchantments Whether the item has enchantments.
     * @return The new override value (true/false) to set, or null to remove the override.
     */
    private Boolean determineGlintOverrideState(Boolean currentOverride, boolean hasEnchantments) {
        if (currentOverride == null) {
            // No override exists: enable glint if not enchanted, disable if enchanted
            return !hasEnchantments;
        } else {
            // Override exists: toggle it (remove if true, set to true if false)
            return currentOverride ? null : true;
        }
    }
}
