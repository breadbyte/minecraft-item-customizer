package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.RenameCommandDispatcher;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.operations.HelpOperations;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RenameCommand implements BaseCommand {
    public static final String RENAME_ARGUMENT = "rename";

    public void register(Permission grant, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var subCommand = InternalHelper.RequirePermissionFor(literal(subCommandName), grant);

        var ArgNodeName = argument(RENAME_ARGUMENT, StringArgumentType.greedyString());
        var ArgNodeResetName = literal("reset");
        var ArgNodeHelpRename = literal("help");

        // model name [name]
        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeName
                .executes(RenameCommandDispatcher::renameItem
                ))));

        // model name reset
        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeResetName
                .executes(RenameCommandDispatcher::resetName
                ))));

        // model name help
        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeHelpRename
                .executes(HelpOperations::RenameHelp
                ))));
    }
}
