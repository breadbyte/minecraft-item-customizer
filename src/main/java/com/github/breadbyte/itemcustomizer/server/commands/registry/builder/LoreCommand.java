package com.github.breadbyte.itemcustomizer.server.commands.registry.builder;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear.ModelWearRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.operations.HelpOperations;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LoreCommand implements BaseCommand {

    private static ModelLoreRunner RUNNER;
    public LoreCommand(ModelLoreRunner runner) {
        RUNNER = runner;
    }


    public static final String LORE_ARGUMENT = "text";
    public static final Permission LORE_PERMISSION = Permission.BASE.chain("lore");

    @Override
    public void register(Permission grant, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var subCommand = InternalHelper.RequirePermissionFor(literal(subCommandName), grant);

        var ArgNodeText = argument(LORE_ARGUMENT, StringArgumentType.greedyString());
        var ArgNodeReset = literal("reset");
        var ArgNodeHelp = literal("help");

        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeText
                .executes(RUNNER::applyLore))));

        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeReset
                .executes(RUNNER::resetLore))));

        dispatcher.register(root
                .then(subCommand
                .then(ArgNodeHelp
                .executes(HelpOperations::LoreHelp))));
    }
}
