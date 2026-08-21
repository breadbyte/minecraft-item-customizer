package com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.IModelApplyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.ModelApplyParams;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.equipment.ModelEquipmentParams;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment.ModelEquipmentOperations;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ModelApplyOperations implements IModelApplyOperations {

    private final ModelEquipmentOperations equipmentOperations = new ModelEquipmentOperations();

    @Override
    public Result<String> apply(ModelApplyParams params) {
        var ns = params.identifier();

        // Get the components for the currently held item
        var item = params.item();

        // Set it to the new model
        NbtCompound nbt = item.getOrCreateNbt();
        nbt.putInt("CustomModelData", params.number());

//        // HACK HACK
//        if (params.model() != null) {
//            if (params.model()._internalIsEquipment()) {
//                var eqp = item.getComponents().get(DataComponentTypes.EQUIPPABLE);
//                if (eqp != null) {
//                    equipmentOperations.toggle(new ModelEquipmentParams(item));
//                }
//            }
//        }
//
//        var modelResult = ModelsIndex.INSTANCE.getExact(ns);
//        CustomModelDefinition model = null;
//
//        if (modelResult.isOk()) { model = modelResult.unwrap(); }
//
//        if (model == null)
//            return Result.ok("Model " + ns + " applied!");
//
//        if (model.madeBy() == null || model.madeBy().isBlank()) {
//            return Result.ok("Model " + model.getName() + " applied!");
//        }
//        return Result.ok("Model " + model.getName() + " made by " + model.madeBy() + " applied!");

        return Result.ok("Applied custom model "+ params.number() + " to item!");
    }

    @Override
    public Result<String> reset(ModelApplyParams params) {
        NbtCompound nbt = params.item().getOrCreateNbt();
        nbt.remove("CustomModelData");

        return Result.ok("Model reset!");
    }
}
