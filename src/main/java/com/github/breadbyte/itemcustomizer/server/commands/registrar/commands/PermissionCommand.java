package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.CommandDefinition;
import com.github.breadbyte.itemcustomizer.server.commands.impl.GrantCommands;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommands;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelSuggestionProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class PermissionCommand implements CommandDefinition<ServerCommandSource> {
    @Override
    public String commandName() {
        return "permission";
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {

        var cmd_root = literal(commandName())
                .requires(Permissions.require(Check.Permission.GRANT.getPermission(), 4));
        var NodeGrant = cmd_root.then(literal("grant"));
        var NodeRevoke = cmd_root.then(literal("revoke"));
        var NodeGet =  cmd_root.then(literal("get"));

        // TODO: Suggest valid namespaces
        var ArgNodeItemNamespace =
                argument("namespace", StringArgumentType.word());

        var ArgNodeItemCategory =
                argument("item_type", StringArgumentType.word())
                .suggests(ModelCategorySuggestionProvider.INSTANCE);

        var ArgNodeItemName =
                argument("item_name", StringArgumentType.string())
                .suggests(ModelSuggestionProvider.INSTANCE);

        // TODO: Suggest online players
        var ArgNodePlayer =
                argument("player", EntityArgumentType.player());

        // permission grant namespace category name player
        dispatcher.register(root
                .then(NodeGrant
                .then(ArgNodeItemNamespace
                .then(ArgNodeItemCategory
                .then(ArgNodeItemName
                .then(ArgNodePlayer
                .executes(GrantCommands::grantModelPerm
                )))))));


        // permission revoke namespace category name player
        dispatcher.register(root
                .then(NodeRevoke
                .then(ArgNodeItemNamespace
                .then(ArgNodeItemCategory
                .then(ArgNodeItemName
                .executes(GrantCommands::revokeModelPerm
                ))))));

        // permission get namespace category name
        dispatcher.register(root
                .then(NodeGet
                .then(ArgNodeItemNamespace
                .then(ArgNodeItemCategory
                .then(ArgNodeItemName
                .executes(ModelCommands::getPermissionNode
                ))))));
    }
}
