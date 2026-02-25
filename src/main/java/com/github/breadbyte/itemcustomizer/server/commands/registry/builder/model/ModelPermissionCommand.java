package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission.ModelPermissionRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNamespaceSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.brigadier.ModelSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class ModelPermissionCommand implements BaseCommand {
    private static ModelPermissionRunner RUNNER;
    public ModelPermissionCommand(ModelPermissionRunner runner) {
        RUNNER = runner;
    }

    public static final String NAMESPACE_ARGUMENT = "namespace";
    public static final String CATEGORY_ARGUMENT = "item_category";
    public static final String NAME_ARGUMENT = "item_name";
    public static final String PLAYER_ARGUMENT = "player";

    public void register(Permission grant, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var subCommand = InternalHelper.RequirePermissionFor(literal(subCommandName), grant);

        var NodeGrant = literal("grant");
        var NodeRevoke = literal("revoke");
        var NodeGet = literal("get");

        var ArgNodeItemNamespace =
                argument(NAMESPACE_ARGUMENT, StringArgumentType.word())
                .suggests(ModelNamespaceSuggestionProvider.INSTANCE);

        var ArgNodeItemCategory =
                argument(CATEGORY_ARGUMENT, StringArgumentType.word())
                .suggests(ModelCategorySuggestionProvider.INSTANCE);

        var ArgNodeItemName =
                argument(NAME_ARGUMENT, StringArgumentType.string())
                .suggests(ModelSuggestionProvider.INSTANCE);

        var ArgNodePlayer =
                argument(PLAYER_ARGUMENT, EntityArgumentType.player());

        // model permission grant namespace category name player
        dispatcher.register(root
                .then(subCommand
                .then(NodeGrant
                .then(ArgNodeItemNamespace
                .then(ArgNodeItemCategory
                .then(ArgNodeItemName
                .then(ArgNodePlayer
                .executes(RUNNER::grantPermission
                ))))))));


        // model permission revoke namespace category name player
        dispatcher.register(root
                .then(subCommand
                .then(NodeRevoke
                .then(ArgNodeItemNamespace
                .then(ArgNodeItemCategory
                .then(ArgNodeItemName
                .then(ArgNodePlayer
                .executes(RUNNER::revokePermission
                ))))))));

        // model permission get namespace category name
        dispatcher.register(root
                .then(subCommand
                .then(NodeGet
                .then(ArgNodeItemNamespace
                .then(ArgNodeItemCategory
                .then(ArgNodeItemName
                .executes(RUNNER::getPermissionNode
                )))))));
    }
}
