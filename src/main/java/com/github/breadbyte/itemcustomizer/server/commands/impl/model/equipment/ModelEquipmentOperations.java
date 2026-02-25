package com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.equipment.IModelEquipmentOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.equipment.ModelEquipmentParams;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModelEquipmentOperations implements IModelEquipmentOperations {
    @Override
    public Result<String> toggle(ModelEquipmentParams params) {
        var item = params.item();
        var itemComps = item.getComponents();

        // If this item is equippable, change the model for the equippable as well.
        if (itemComps.contains(DataComponentTypes.EQUIPPABLE)) {
            // Get the first equippable component
            var equippable = itemComps.get(DataComponentTypes.EQUIPPABLE);
            var model = itemComps.get(DataComponentTypes.ITEM_MODEL);

            // equippable cannot be null at this point, we just checked
            if (equippable.assetId().isPresent()) {
                if (equippable.assetId().get().getValue().equals(model))
                    return Result.err(new Reason.InternalError("Model is already set to equippable asset, no changes made."));
            }

            CreateEquippableModel(item, equippable, model);
            return Result.ok("Model equipment texture applied!");
        } else {
            return Result.err(new Reason.NotAnError("Item is not equippable!"));
        }
    }

    @Override
    public Result<String> reset(ModelEquipmentParams params) {
        var item = params.item();
        var itemComps = item.getComponents();

        // If this item is equippable, change the model for the equippable as well.
        if (itemComps.contains(DataComponentTypes.EQUIPPABLE)) {
            var defaultModel = item.getDefaultComponents().get(DataComponentTypes.ITEM_MODEL);
            var defaultStack = item.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE);
            item.remove(DataComponentTypes.EQUIPPABLE);
            if (defaultStack == null) return Result.ok("Model equipment texture reset!");

            CreateEquippableModel(item, defaultStack, defaultModel);
            return Result.ok("Model equipment texture reset!");
        } else {
            return Result.err(new Reason.NotAnError("Item is not equippable!"));
        }
    }


    private void CreateEquippableModel(ItemStack item, EquippableComponent equippable, Identifier model) {
        var eqAsset = RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, model);
        var newEquippableBuilder = EquippableComponent.builder(equippable.slot())
                .equipSound(equippable.equipSound())
                .model(eqAsset)
                .dispensable(equippable.dispensable())
                .swappable(equippable.swappable())
                .damageOnHurt(equippable.damageOnHurt());

        if (equippable.cameraOverlay().isPresent())
            newEquippableBuilder.cameraOverlay(equippable.cameraOverlay().get());
        if (equippable.allowedEntities().isPresent())
            newEquippableBuilder.allowedEntities(equippable.allowedEntities().get());

        var newEquippable = newEquippableBuilder.build();
        item.set(DataComponentTypes.EQUIPPABLE, newEquippable);
    }
}
