package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.brigadier.ModelNamespaceSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.group.ModelGroupRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock.ModelLockRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelGroupCommand implements BaseCommand {

    public static String GROUP_NAME_ARGUMENT = "GROUP_NAME";
    public static String PLAYER_NAME_ARGUMENT = "GROUP_PLAYER_NAME";

    private static ModelGroupRunner RUNNER;
    public ModelGroupCommand(ModelGroupRunner runner) {
        RUNNER = runner;
    }

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        // The 'root' here is already the 'model' command.
        // We need to apply the permission to the 'lock' and 'unlock' subcommands.

        var CreateNode = InternalHelper.RequirePermissionFor(literal("create"), Permission.GROUP.chain("create"));
        var RemoveNode = InternalHelper.RequirePermissionFor(literal("remove"), Permission.GROUP.chain("remove"));
        var ListNode = InternalHelper.RequirePermissionFor(literal("list"), Permission.GROUP.chain("list"));

        var AdminNode = InternalHelper.RequirePermissionFor(literal("list"), Permission.GROUP.chain("opadmin"));
        var AdminAddNode = InternalHelper.RequirePermissionFor(literal("add"), Permission.GROUP.chain("opadmin"));
        var AdminRemoveNode = InternalHelper.RequirePermissionFor(literal("remove"), Permission.GROUP.chain("opadmin"));

        var GroupName = CommandManager.argument(GROUP_NAME_ARGUMENT, StringArgumentType.word())
                .suggests(ModelNamespaceSuggestionProvider.INSTANCE);


        var GroupNode = InternalHelper.RequirePermissionFor(literal("group"), permission);
        var GroupAddNode = InternalHelper.RequirePermissionFor(literal("add"), permission);
        var GroupRemoveNode = InternalHelper.RequirePermissionFor(literal("remove"), permission);

        var GroupLock = InternalHelper.RequirePermissionFor(literal("lock"), permission);
        var GroupUnlock = InternalHelper.RequirePermissionFor(literal("unlock"), permission);

        var PlayerSuggestionNode = CommandManager.argument(PLAYER_NAME_ARGUMENT, EntityArgumentType.player());

        dispatcher.register(root
                .then(CreateNode
                        .then(CommandManager.argument("group_name", StringArgumentType.string())
                        .executes(RUNNER::AddGroup))));

        dispatcher.register(root
                .then(RemoveNode
                        .then(GroupName
                        .executes(RUNNER::RemoveGroup))));

        dispatcher.register(root
                .then(ListNode
                        .executes(RUNNER::ListGroup)));

        dispatcher.register(root
                .then(AdminNode
                        .then(GroupName
                                .then(AdminAddNode
                                        .then(PlayerSuggestionNode.executes(RUNNER::PromoteAdmin)))
                                .then(AdminRemoveNode
                                        .then(PlayerSuggestionNode.executes(RUNNER::DemoteAdmin))))));

        dispatcher.register(root
                .then(GroupNode
                        .then(GroupName
                                .then(GroupAddNode.then(PlayerSuggestionNode.executes(RUNNER::AddToGroup)))
                                .then(GroupRemoveNode.then(PlayerSuggestionNode.executes(RUNNER::RemoveFromGroup))))));

        dispatcher.register(root
                .then(GroupNode
                        .then(GroupName
                                .then(GroupLock.executes(RUNNER::LockToGroup)))));
    }
}
