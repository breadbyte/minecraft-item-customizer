package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommandsPreChecked;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelNamespaceSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelSuggestionProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelApplyCommand implements BaseCommand {

    public static final String NAMESPACE_ARGUMENT = "namespace";
    public static final String ITEM_CATEGORY_ARGUMENT = "item_category";
    public static final String ITEM_NAME_ARGUMENT = "item_name";
    public static final String EQUIPMENT_TEXTURE_ARGUMENT = "change_equippable_texture";
    public static final String COLOR_ARGUMENT = "color";

    @Override
    public void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var NamespaceNode = CommandManager.argument(NAMESPACE_ARGUMENT, StringArgumentType.string())
                .suggests(ModelNamespaceSuggestionProvider.INSTANCE);

        var ApplyNode = literal("apply");
        var ItemCategoryNode = CommandManager.argument(ITEM_CATEGORY_ARGUMENT, StringArgumentType.word())
                .suggests(ModelCategorySuggestionProvider.INSTANCE);

        var ItemNameNode = CommandManager.argument(ITEM_NAME_ARGUMENT, StringArgumentType.string())
                .suggests(ModelSuggestionProvider.INSTANCE);

        var ItemEquipmentTextureBooleanNode = CommandManager.argument(EQUIPMENT_TEXTURE_ARGUMENT, BoolArgumentType.bool());

        // This has to be added to the Apply command to prevent being used for vanilla items
        var ColorNode = CommandManager.argument(COLOR_ARGUMENT, IntegerArgumentType.integer());

        var ResetNode = literal("reset");

        // model apply item_namespace item_category item_name
        // model apply item_namespace item_category item_name bool
        // model apply item_namespace item_category item_name bool color

        // model apply item_namespace old/item/format
        // model apply item_namespace old/item/format bool
        // model apply item_namespace old/item/format bool color
        dispatcher.register(_root
                .then(ApplyNode
                .then(NamespaceNode
                .then(ItemCategoryNode
                        .executes(ModelCommandsPreChecked::applyModel)
                        .then(ItemEquipmentTextureBooleanNode
                                .executes(ModelCommandsPreChecked::applyModel)
                                .then(ColorNode
                                .executes(ModelCommandsPreChecked::applyModel)))
                .then(ItemNameNode
                      .executes(ModelCommandsPreChecked::applyModel)
                        .then(ItemEquipmentTextureBooleanNode
                            .executes(ModelCommandsPreChecked::applyModel))
                .then(ColorNode
                .executes(ModelCommandsPreChecked::applyModel)))))));

        // model reset
        dispatcher.register(_root
                .then(ResetNode
                .executes(ModelCommandsPreChecked::resetModel)));
    }
}
