package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.operations.model.LockOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class LockCommandDispatcher {
    public static int lockModel(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, LockOperations::lockModel, StackRequirement.REQUIRED_MAINHAND,"Model locked!");
    }

    public static int unlockModel(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, LockOperations::unlockModel, StackRequirement.SPECIAL_UNLOCK,"Model unlocked!");
    }
}
