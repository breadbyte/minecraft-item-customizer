package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.operations.model.GlintOperations;
import com.github.breadbyte.itemcustomizer.server.operations.model.NamespaceOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class NamespaceCommandDispatcher {

    public static int NamespaceRegister(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, NamespaceOperations::registerSuggestions, StackRequirement.NONE, "");
    }

    public static int NamespaceClear(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, NamespaceOperations::clearSuggestions, StackRequirement.NONE, "All custom model data suggestions cleared");
    }

    public static int NamespaceRemove(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, NamespaceOperations::removeNamespace, StackRequirement.NONE, "Namespace suggestion removed!");
    }
}
