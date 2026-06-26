package com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.IModelCopyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.ModelCopyParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

public class ModelCopyOperations implements IModelCopyOperations {

    @Override
    public Result<String> copy(ModelCopyParams params) {
        var offHand = params.offHand();
        var offHandComps = offHand.getComponents();

        // 1. Gather all target slots based on the destination
        java.util.List<ItemStack> targets = new java.util.ArrayList<>();

        switch (params.copyTo()) {
            case mainhand -> targets.add(params.mainHand());
            case hotbar -> {
                for (int i = 0; i < 9; i++) {
                    targets.add(params.player().getInventory().getStack(i));
                }
            }
            case inventory -> {
                for (int i = 0; i < 36; i++) {
                    var slot = params.player().getInventory().getStack(i);
                    if (slot.getItem().getDefaultStack().getItemName().equals(offHand.getItem().getDefaultStack().getItemName())) {
                        targets.add(slot);
                    }
                }
            }
        }

        // 2. Define the action to take using a local consumer to avoid generic mismatch bugs
        java.util.function.Consumer<ItemStack> copyAction = switch (params.copyWhat()) {
            case model -> slot -> slot.set(DataComponentTypes.ITEM_MODEL, offHandComps.get(DataComponentTypes.ITEM_MODEL));
            case name -> slot -> slot.set(DataComponentTypes.CUSTOM_NAME, offHandComps.get(DataComponentTypes.CUSTOM_NAME));
            case lore -> slot -> slot.set(DataComponentTypes.LORE, offHandComps.get(DataComponentTypes.LORE));
            case all -> slot -> copyAll(new ModelCopyParams(params.player(), slot, offHand, COPY_TO_ARGUMENT.mainhand, COPY_WHAT_ARGUMENT.all));
        };

        // 3. Apply the action to all collected items
        for (var targetSlot : targets) {
            copyAction.accept(targetSlot);
        }

        return Result.ok();
    }

    @Override
    public Result<String> copyAll(ModelCopyParams params) {
        var mainHand = params.mainHand();
        var offHandComps = params.offHand().getComponents();

        // List of all component types you want to synchronize
        var componentsToCopy = java.util.List.of(
                DataComponentTypes.ITEM_MODEL,
                DataComponentTypes.EQUIPPABLE,
                DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,
                DataComponentTypes.CUSTOM_MODEL_DATA,
                DataComponentTypes.CUSTOM_NAME,
                DataComponentTypes.LORE,
                DataComponentTypes.DYED_COLOR
        );

        // Loop through and copy or remove each component type
        for (var type : componentsToCopy) {
            copyComponent(mainHand, offHandComps, type);
        }

        return Result.ok();
    }

    // Private helper to safely capture the wildcard <T> and satisfy the compiler
    private <T> void copyComponent(ItemStack target, net.minecraft.component.ComponentMap sourceComps, net.minecraft.component.ComponentType<T> type) {
        T value = sourceComps.get(type);
        if (value != null) {
            target.set(type, value);
        } else {
            target.remove(type);
        }
    }
}
