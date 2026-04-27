package com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.IModelGlintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.ModelGlintParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

import javax.xml.crypto.Data;

import static java.lang.Boolean.TRUE;

public class ModelGlintOperations implements IModelGlintOperations {
    @Override
    public Result<String> toggle(ModelGlintParams params) {
        var playerItem = params.item();
        var override = playerItem.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);

        if (isGlintDefaultForItem(playerItem)) {
            toggleDefaultGlintItem(playerItem, override);
        } else {
            boolean shouldEnableGlint = playerItem.getComponents().get(DataComponentTypes.ENCHANTABLE) == null
                    || !playerItem.hasEnchantments();
            toggleGlintOverride(playerItem, override, shouldEnableGlint);
        }

        return Result.ok();
    }

    private void toggleDefaultGlintItem(ItemStack playerItem, Boolean override) {
        if (!doesGlintExistOnItem(playerItem)) {
            toggleGlintOverride(playerItem, override, false);
            return;
        }

        if (override == null) {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,
                    playerItem.getDefaultComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE));
        } else {
            boolean current = override;
            boolean defaultGlint = playerItem.getDefaultComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE).booleanValue();
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, current != defaultGlint);
        }
    }

    private void toggleGlintOverride(ItemStack playerItem, Boolean override, boolean valueWhenNull) {
        if (override == null) {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, valueWhenNull);
        } else {
            playerItem.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        }
    }

    private Boolean isGlintDefaultForItem(ItemStack item) {
        return (item.getRarity() == Rarity.EPIC || item.getItem().getDefaultStack().hasGlint());
    }

    private Boolean doesGlintExistOnItem(ItemStack item) {
        return item.getDefaultComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE) != null;
    }
}
