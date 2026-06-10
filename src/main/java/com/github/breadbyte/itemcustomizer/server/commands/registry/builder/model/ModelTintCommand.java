package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.tint.ModelTintRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.HexColorArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelTintCommand implements BaseCommand {

    private static ModelTintRunner RUNNER;
    public ModelTintCommand(ModelTintRunner runner) {
        RUNNER = runner;
    }

    public static final String TINT_INDEX_ARGUMENT = "tint_index";
    public static final String TINT_COLOR_ARGUMENT = "tint_color";

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        // The 'root' here is already the 'model' command.
        // We need to apply the permission to the 'tint' and 'reset' subcommands.

        var TintNode = InternalHelper.RequirePermissionFor(literal("tint"), permission);
        var TintIndexNode = CommandManager.argument(TINT_INDEX_ARGUMENT, IntegerArgumentType.integer(0));
        var TintColorNode = CommandManager.argument(TINT_COLOR_ARGUMENT, HexColorArgumentType.hexColor());
        var TintResetNode = InternalHelper.RequirePermissionFor(literal("reset"), permission);


        // Current valid commands:
        //  tint [index] [number]
        //  tint reset
        dispatcher.register(root
                .then(TintNode
                .then(TintIndexNode
                .then(TintColorNode
                .executes(RUNNER::applyTint)))));

        dispatcher.register(root
                .then(TintNode
                .then(TintResetNode
                .executes(RUNNER::resetTint))));
    }
}