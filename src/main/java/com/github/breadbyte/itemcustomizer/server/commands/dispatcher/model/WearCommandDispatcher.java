package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.operations.model.TintOperations;
import com.github.breadbyte.itemcustomizer.server.operations.model.WearOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.command.ServerCommandSource;

public class WearCommandDispatcher {
    public static int toggleWearable(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, WearOperations::ToggleWearable, StackRequirement.REQUIRED_MAINHAND,"");
    }
}
