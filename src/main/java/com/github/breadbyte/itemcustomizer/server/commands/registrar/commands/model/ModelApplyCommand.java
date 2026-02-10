package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommands;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelSuggestionProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelApplyCommand implements BaseCommand {
    @Override
    public void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var ApplyNode = literal("apply");
        var ItemCategoryNode = CommandManager.argument("item_category", StringArgumentType.word())
                .suggests(ModelCategorySuggestionProvider.INSTANCE);

        var ItemNameNode = CommandManager.argument("item_name", StringArgumentType.string())
                .suggests(ModelSuggestionProvider.INSTANCE);

        // This has to be added to the Apply command to prevent being used for vanilla items
        var ColorNode = CommandManager.argument("color", IntegerArgumentType.integer());

        var ResetNode = literal("reset");

        // model apply item_category item_name
        // model apply item_category item_name color
        dispatcher.register(_root
                .then(ApplyNode
                .then(ItemCategoryNode
                .then(ItemNameNode
                      .executes(ModelCommands::applyModel))
                .then(ColorNode
                .executes(ModelCommands::applyModel)))));

        // model reset
        dispatcher.register(_root
                .then(ResetNode
                .executes(ModelCommands::resetModel)));
    }
}
