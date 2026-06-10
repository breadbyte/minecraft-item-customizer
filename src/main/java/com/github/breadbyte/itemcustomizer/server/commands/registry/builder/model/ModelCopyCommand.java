package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy.ModelCopyRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelCopyCommand implements BaseCommand {

    private static ModelCopyRunner RUNNER;
    public ModelCopyCommand(ModelCopyRunner runner) {
        RUNNER = runner;
    }

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        // The 'root' here is already the 'model' command.
        // We need to apply the permission to the 'copy' subcommand.

        var CopyNode = InternalHelper.RequirePermissionFor(literal("copy"), permission);
        var NameNode = literal("name");
        var LoreNode = literal("lore");
        var ModelNode = literal("model");

        // model copy
        dispatcher.register(root
                .then(CopyNode
                .executes(RUNNER::copyAll)
                .then(NameNode.executes(RUNNER::copyName))
                .then(LoreNode.executes(RUNNER::copyLore))
                .then(ModelNode.executes(RUNNER::copyModel))));
    }
}