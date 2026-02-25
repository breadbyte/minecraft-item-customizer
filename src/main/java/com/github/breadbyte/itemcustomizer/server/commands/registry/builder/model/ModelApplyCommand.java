package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply.ModelApplyRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNamespaceSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
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

    private static ModelApplyRunner RUNNER;
    public ModelApplyCommand(ModelApplyRunner runner) {
        RUNNER = runner;
    }


    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var NamespaceNode = CommandManager.argument(NAMESPACE_ARGUMENT, StringArgumentType.string())
                .suggests(ModelNamespaceSuggestionProvider.INSTANCE);

        var ApplyNode = literal("apply");
        var ItemCategoryNode = CommandManager.argument(ITEM_CATEGORY_ARGUMENT, StringArgumentType.string())
                .suggests(ModelCategorySuggestionProvider.INSTANCE);

        var ItemNameNode = CommandManager.argument(ITEM_NAME_ARGUMENT, StringArgumentType.string())
                .suggests(ModelSuggestionProvider.INSTANCE);

        var ResetNode = literal("reset");

        // model apply item_namespace item_category item_name
        // model apply item_namespace old/item/format
        dispatcher.register(_root
                .then(ApplyNode
                .then(NamespaceNode
                .then(ItemCategoryNode
                        .executes(RUNNER::applyModel)
                .then(ItemNameNode
                        .executes(RUNNER::applyModel))))));

        // model reset
        dispatcher.register(_root
                .then(ResetNode
                .executes(RUNNER::resetModel)));
    }
}
