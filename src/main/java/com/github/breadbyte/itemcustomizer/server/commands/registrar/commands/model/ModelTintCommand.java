package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommands;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelTintCommand implements BaseCommand {
    @Override
    public void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var TintNode = literal("tint");
        var TintIndexNode = CommandManager.argument("tint_index", IntegerArgumentType.integer(0));
        var TintColorNode = CommandManager.argument("tint_color", IntegerArgumentType.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
        var TintResetNode = literal("reset");


        // Current valid commands:
        //  tint [index] [number]
        //  tint reset
        dispatcher.register(_root
                .then(TintNode
                .then(TintIndexNode
                .then(TintColorNode
                .executes(ModelCommands::tintModel)))));

        dispatcher.register(_root
                .then(TintNode
                .then(TintResetNode
                .executes(ModelCommands::tintReset))));
    }
}
