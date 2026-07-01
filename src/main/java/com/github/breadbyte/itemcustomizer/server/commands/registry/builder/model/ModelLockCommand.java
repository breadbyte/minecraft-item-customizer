package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNamespaceSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock.ModelLockRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.locks.Lock;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelLockCommand implements BaseCommand {

    private static ModelLockRunner RUNNER;
    public ModelLockCommand(ModelLockRunner runner) {
        RUNNER = runner;
    }

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        // The 'root' here is already the 'model' command.
        // We need to apply the permission to the 'lock' and 'unlock' subcommands.

        var LockNode = InternalHelper.RequirePermissionFor(literal("lock"), permission);
        var UnlockNode = InternalHelper.RequirePermissionFor(literal("unlock"), permission);

        dispatcher.register(root
                .then(LockNode
                        .executes(RUNNER::lockModel)));

        dispatcher.register(root
                .then(UnlockNode
                        .executes(RUNNER::unlockModel)));
    }
}