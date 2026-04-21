package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply.ModelApplyRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNamespaceSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNodeSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelApplyCommand implements BaseCommand {

    public static final String NAMESPACE_ARGUMENT = "namespace";
    public static final String ITEM_CATEGORY_ARGUMENT = "item_category";
    public static final String ITEM_PATH_ARGUMENT = "item_path";
    public static final String NODE_PREFIX = "node";
    public static final int MAX_AUTOCOMPLETE_NODES = 7;
    public static final String EQUIPMENT_TEXTURE_ARGUMENT = "change_equippable_texture";
    public static final String COLOR_ARGUMENT = "color";

    private final ModelApplyRunner RUNNER;
    public ModelApplyCommand(ModelApplyRunner runner) {
        RUNNER = runner;
    }


    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var NamespaceNode = CommandManager.argument(NAMESPACE_ARGUMENT, StringArgumentType.string())
                .suggests(ModelNamespaceSuggestionProvider.INSTANCE);

        var ApplyNode = literal("apply");
        var ResetNode = literal("reset");

        // Build the dynamic node chain
        RequiredArgumentBuilder<ServerCommandSource, String> lastNode = null;
        for (int i = MAX_AUTOCOMPLETE_NODES; i >= 1; i--) {
            var node = CommandManager.argument(NODE_PREFIX + i, StringArgumentType.string())
                    .suggests(ModelNodeSuggestionProvider.INSTANCE)
                    .executes(RUNNER::applyModel);
            if (lastNode != null) {
                node.then(lastNode);
            }
            lastNode = node;
        }

        // model apply itemNamespace itemCategory node1 [node2] ... [node6]
        dispatcher.register(_root
                .then(ApplyNode
                .then(NamespaceNode
                .then(lastNode))));

        // model reset
        dispatcher.register(_root
                .then(ResetNode
                .executes(RUNNER::resetModel)));
    }
}
