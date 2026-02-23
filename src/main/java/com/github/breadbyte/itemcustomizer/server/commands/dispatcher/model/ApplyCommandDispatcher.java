package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.operations.model.ApplyOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ApplyCommandDispatcher {
    public static int applyModelPath(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, ApplyOperations::applyModelPath, StackRequirement.REQUIRED_MAINHAND, "");
    }

    public static int applyModelModern(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, ApplyOperations::applyModelModern, StackRequirement.REQUIRED_MAINHAND, "");
    }

    public static int resetModel(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, ApplyOperations::resetModel, StackRequirement.REQUIRED_MAINHAND, "Model reset to default!");
    }
}
