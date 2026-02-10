package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommands;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelGlintCommand implements BaseCommand {
    @Override
    public void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var GlintNode = literal("glint");

        dispatcher.register(_root
                .then(GlintNode
                .executes(ModelCommands::applyGlint)));
    }
}
