package com.github.breadbyte.itemcustomizer.server.commands.dispatcher;

import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.operations.LoreOperations;
import com.github.breadbyte.itemcustomizer.server.operations.RenameOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class RenameCommandDispatcher {

    public static int renameItem(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, RenameOperations::renameItem, StackRequirement.REQUIRED_MAINHAND,"Item renamed!");
    }

    public static int resetName(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, RenameOperations::resetName, StackRequirement.REQUIRED_MAINHAND,"Item name reset!");
    }
}

