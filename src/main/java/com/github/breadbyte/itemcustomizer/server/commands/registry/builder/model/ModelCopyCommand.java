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
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var CopyNode = literal("copy");

        // model copy
        dispatcher.register(_root
                .then(CopyNode
                .executes(RUNNER::copyOffhandToMainhand)));
    }
}
