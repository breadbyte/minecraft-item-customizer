package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock.ModelLockRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelLockCommand implements BaseCommand {

    private static ModelLockRunner RUNNER;
    public ModelLockCommand(ModelLockRunner runner) {
        RUNNER = runner;
    }

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var LockNode = literal("lock");
        var UnlockNode = literal("unlock");

        dispatcher.register(_root
                .then(LockNode
                        .executes(RUNNER::lockModel)));

        dispatcher.register(_root
                .then(UnlockNode
                        .executes(RUNNER::unlockModel)));
    }
}
