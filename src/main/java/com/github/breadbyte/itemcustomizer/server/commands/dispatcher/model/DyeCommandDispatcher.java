package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelDyeCommand;
import com.github.breadbyte.itemcustomizer.server.operations.model.DyeOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DyeCommandDispatcher {
    public static int dyeModel(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, DyeOperations::DyeModel, StackRequirement.REQUIRED_MAINHAND, "Dye applied!");

    }

    public static int dyeReset(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, DyeOperations::ResetDye, StackRequirement.REQUIRED_MAINHAND, "Dye reset!");
    }
}
