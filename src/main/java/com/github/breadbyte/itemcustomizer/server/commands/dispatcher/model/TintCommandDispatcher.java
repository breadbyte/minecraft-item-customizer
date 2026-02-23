package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.operations.model.TintOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class TintCommandDispatcher {
    public static int tintModel(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, TintOperations::TintModel, StackRequirement.REQUIRED_MAINHAND,"");
    }

    public static int tintReset(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, TintOperations::TintReset, StackRequirement.REQUIRED_MAINHAND,"Tint cleared!");
    }
}
