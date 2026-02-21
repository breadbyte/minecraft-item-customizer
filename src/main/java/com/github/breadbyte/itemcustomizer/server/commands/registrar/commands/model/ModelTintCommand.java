package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model;

import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommandsPreChecked;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.HexColorArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelTintCommand implements BaseCommand {

    public static final String TINT_INDEX_ARGUMENT = "tint_index";
    public static final String TINT_COLOR_ARGUMENT = "tint_color";

    @Override
    public void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var TintNode = literal("tint");
        var TintIndexNode = CommandManager.argument(TINT_INDEX_ARGUMENT, IntegerArgumentType.integer(0));
        var TintColorNode = CommandManager.argument(TINT_COLOR_ARGUMENT, HexColorArgumentType.hexColor());
        var TintResetNode = literal("reset");


        // Current valid commands:
        //  tint [index] [number]
        //  tint reset
        dispatcher.register(_root
                .then(TintNode
                .then(TintIndexNode
                .then(TintColorNode
                .executes(ModelCommandsPreChecked::tintModel)))));

        dispatcher.register(_root
                .then(TintNode
                .then(TintResetNode
                .executes(ModelCommandsPreChecked::tintReset))));
    }
}
