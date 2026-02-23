package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.operations.model.GlintOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class GlintCommandDispatcher {
    public static int toggleGlint(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, GlintOperations::toggleGlint, StackRequirement.REQUIRED_MAINHAND, "Glint toggled!");
    }
}
