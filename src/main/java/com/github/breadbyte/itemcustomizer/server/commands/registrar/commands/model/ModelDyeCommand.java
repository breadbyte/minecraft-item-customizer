package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommandsPreChecked;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.HexColorArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelDyeCommand implements BaseCommand {

    public static final String COLOR_ARGUMENT = "dye_color";

    @Override
    public void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var DyeNode = literal("dye");
        var DyeColorNode = CommandManager.argument(COLOR_ARGUMENT, HexColorArgumentType.hexColor());
        var DyeResetNode = literal("reset");


        // Current valid commands:
        //  dye [index] [number]
        //  dye reset
        dispatcher.register(_root
                .then(DyeNode
                        .then(DyeColorNode
                                .then(DyeColorNode
                                        .executes(ModelCommandsPreChecked::dyeModel)))));

        dispatcher.register(_root
                .then(DyeNode
                        .then(DyeResetNode
                                .executes(ModelCommandsPreChecked::dyeReset))));
    }
}
