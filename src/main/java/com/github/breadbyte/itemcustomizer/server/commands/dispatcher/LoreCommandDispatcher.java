package com.github.breadbyte.itemcustomizer.server.commands.dispatcher;

import com.github.breadbyte.itemcustomizer.server.operations.LoreOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class LoreCommandDispatcher {

    public static int addLore(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, LoreOperations::add, StackRequirement.REQUIRED_MAINHAND, "Lore added!");
    }

    public static int resetLore(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, LoreOperations::reset, StackRequirement.REQUIRED_MAINHAND, "Lore reset!");

    }
}

