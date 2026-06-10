package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNodeSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission.ModelPermissionRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNamespaceSuggestionProvider;
//import com.github.breadbyte.itemcustomizer.server.brigadier.ModelSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.MAX_AUTOCOMPLETE_NODES;
import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.NODE_PREFIX;
import static net.minecraft.server.command.CommandManager.*;

public class ModelPermissionCommand implements BaseCommand {
    private static ModelPermissionRunner RUNNER;
    public ModelPermissionCommand(ModelPermissionRunner runner) {
        RUNNER = runner;
    }

    public static final String NAMESPACE_ARGUMENT = "namespace";
    public static final String PLAYER_ARGUMENT = "player";

    public void register(Permission grant, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var subCommand = InternalHelper.RequirePermissionFor(literal(subCommandName), grant);

        var NodeGrant = literal("grant");
        var NodeRevoke = literal("revoke");
        var NodeGet =  ("get");

        var ArgNodeItemNamespace =
                argument(NAMESPACE_ARGUMENT, StringArgumentType.word())
                .suggests(ModelNamespaceSuggestionProvider.INSTANCE);

        RequiredArgumentBuilder<ServerCommandSource, String> lastNode = null;
        for (int i = MAX_AUTOCOMPLETE_NODES; i >= 1; i--) {
            var node = CommandManager.argument(NODE_PREFIX + i, StringArgumentType.string())
                    .suggests(ModelNodeSuggestionProvider.INSTANCE)
                    .executes(RUNNER::grantPermission);
            if (lastNode != null) {
                node.then(lastNode);
            }
            lastNode = node;
        }

        var ArgNodePlayer =
                argument(PLAYER_ARGUMENT, EntityArgumentType.player());

        // todo: this still grants the permission,
        //  but it's kept for reference instead of actually doing anything meaningful with it

        // model permission grant player ...
        dispatcher.register(root
                .then(subCommand
                .then(NodeGrant
                .then(ArgNodePlayer
                .then(ArgNodeItemNamespace
                .then(lastNode))))));

        // model permission revoke player ...
        dispatcher.register(root
                .then(subCommand
                .then(NodeRevoke
                .then(ArgNodePlayer
                .then(ArgNodeItemNamespace
                .then(lastNode))))));

        // model permission get namespace category name
        // fixme: remove the explicit permission cache, then we talk
//        dispatcher.register(root
//                .then(subCommand
//                .then(NodeGet
//                .then(ArgNodePlayer
//                .then(ArgNodeItemNamespace
//                .then(lastNode).executes(RUNNER::getPermissionNode))))));
    }
}
