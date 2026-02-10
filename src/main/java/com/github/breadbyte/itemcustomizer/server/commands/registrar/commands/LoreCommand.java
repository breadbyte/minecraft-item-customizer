package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.impl.LoreCommandsPreChecked;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.operations.HelpOperations;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LoreCommand implements BaseCommand {
    @Override
    public void register(Check.Permission grant, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var subCommand = InternalHelper.RequirePermissionFor(literal(subCommandName), grant);

        var ArgNodeText = argument("text", StringArgumentType.greedyString());
        var ArgNodeReset = literal("reset");
        var ArgNodeHelp = literal("help");

        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeText
                .executes(LoreCommandsPreChecked::addLore))));

        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeReset
                .executes(LoreCommandsPreChecked::resetLore))));

        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeHelp
                .executes(HelpOperations::LoreHelp))));
    }
}
