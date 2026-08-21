package com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.IModelWearOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.ModelWearParams;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import static com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.ModelWearSlot.*;

public class ModelWearOperations implements IModelWearOperations {
    @Override
    public Result<String> apply(ModelWearParams params) {
        var playerItem = params.item();
        var player = params.player();

        ItemStack mainHandStack = player.getEquippedStack(EquipmentSlot.MAINHAND);

        switch (params.slot()) {
            case CHEST -> {
                ItemStack current = player.getEquippedStack(EquipmentSlot.CHEST);
                player.equipStack(EquipmentSlot.CHEST, params.item().copy());
                player.equipStack(EquipmentSlot.MAINHAND, current.copy());
                return Result.ok("Equipped as a chestplate");
            }
            case LEGS -> {
                ItemStack current = player.getEquippedStack(EquipmentSlot.LEGS);
                player.equipStack(EquipmentSlot.LEGS, params.item());
                player.equipStack(EquipmentSlot.MAINHAND, current.copy());
                return Result.ok("Equipped as leggings");
            }
            case FEET -> {
                ItemStack current = player.getEquippedStack(EquipmentSlot.FEET);
                player.equipStack(EquipmentSlot.FEET, params.item());
                player.equipStack(EquipmentSlot.MAINHAND, current.copy());
                return Result.ok("Equipped as boots");
            }

            default -> {
                ItemStack current = player.getEquippedStack(EquipmentSlot.HEAD);
                player.equipStack(EquipmentSlot.HEAD, params.item());
                player.equipStack(EquipmentSlot.MAINHAND, current.copy());
                return Result.ok("Equipped as headgear");
            }
        }
    }
}
