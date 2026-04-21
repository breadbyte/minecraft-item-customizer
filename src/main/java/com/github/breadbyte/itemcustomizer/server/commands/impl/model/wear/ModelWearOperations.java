package com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.IModelWearOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.ModelWearParams;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;

public class ModelWearOperations implements IModelWearOperations {
    @Override
    public Result<String> apply(ModelWearParams params) {
        var playerItem = params.item();
        var paramSlot = params.slot();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();
        var equippableComponent = itemComps.get(DataComponentTypes.EQUIPPABLE);

        // If the component doesn't exist, add it, otherwise, remove it
        if (equippableComponent == null) {
            if (paramSlot != null) {
                switch (paramSlot) {
                    case CHEST -> {
                        playerItem.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.CHEST).build());
                        return Result.ok("You can now equip this item as a chestplate");
                    }
                    case LEGS -> {
                        playerItem.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.LEGS).build());
                        return Result.ok("You can now equip this item as leggings");
                    }
                    case FEET -> {
                        playerItem.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.FEET).build());
                        return Result.ok("You can now equip this item as boots");
                    }

                    default -> {
                        playerItem.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD).build());
                        return Result.ok("You can now equip this item as headgear");
                    }
                }
            }
        } else {
            // If the default item did not have the component, and we have it right now, remove it
            if (playerItem.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE) == null) {
                playerItem.remove(DataComponentTypes.EQUIPPABLE);
            } else {
                playerItem.set(DataComponentTypes.EQUIPPABLE, playerItem.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE));
            }
            return Result.ok("Item is no longer equippable");
        }

        // Failsafe. I don't know how it could fail here, though
        playerItem.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD).build());
        return Result.ok("Item is now equippable");
    }
}
