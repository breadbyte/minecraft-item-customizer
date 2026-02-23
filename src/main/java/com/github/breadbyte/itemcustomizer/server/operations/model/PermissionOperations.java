package com.github.breadbyte.itemcustomizer.server.operations.model;

import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelPermissionCommand;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionOperations {

    public static Result<String> GrantPermission(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var namespace = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAMESPACE_ARGUMENT, String.class));
        var itemType = String.valueOf(ctx.getArgument(ModelPermissionCommand.CATEGORY_ARGUMENT, String.class));
        var itemName = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAME_ARGUMENT, String.class));
        var playerArg = ctx.getArgument(ModelPermissionCommand.PLAYER_ARGUMENT, EntitySelector.class);
        var cmdSrc = ctx.getSource();

        ServerPlayerEntity targetPlayer = null;
        try {
            targetPlayer = playerArg.getPlayer(cmdSrc);
        } catch (CommandSyntaxException e) {
            return Result.err(new Reason.InternalError("Player must be online!"));
        }

        NamespaceCategory ns = new NamespaceCategory(namespace, itemType);
        if (Luckperms.IsLuckpermsPresent()) {
            Luckperms.GrantPermission(targetPlayer, Permission.CUSTOMIZE.chain(ns.getPermissionNode()).getPermission());
            return Result.ok();
        }
        else {
            return Result.err(new Reason.InternalError("LuckPerms not present, grant manually instead: " + Permission.CUSTOMIZE.chain(ns.getPermissionNode()).getPermission()));
        }
    }

    public static Result<String> RevokePermission(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var namespace = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAMESPACE_ARGUMENT, String.class));
        var itemType = String.valueOf(ctx.getArgument(ModelPermissionCommand.CATEGORY_ARGUMENT, String.class));
        var itemName = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAME_ARGUMENT, String.class));
        var playerArg = ctx.getArgument(ModelPermissionCommand.PLAYER_ARGUMENT, EntitySelector.class);
        var cmdSrc = ctx.getSource();

        ServerPlayerEntity targetPlayer = null;
        try {
            targetPlayer = playerArg.getPlayer(cmdSrc);
        } catch (CommandSyntaxException e) {
            return Result.err(new Reason.InternalError("Player must be online!"));
        }

        NamespaceCategory ns = new NamespaceCategory(namespace, itemType);
        if (Luckperms.IsLuckpermsPresent()) {
            Luckperms.RevokePermission(targetPlayer, Permission.CUSTOMIZE.chain(ns.getPermissionNode()));
            return Result.ok();
        }
        else {
            return Result.err(new Reason.InternalError("LuckPerms not present, revoke manually instead: " + Permission.CUSTOMIZE.chain(ns.getPermissionNode()).getPermission()));
        }
    }

    public static Result<String> GetPermissionNode(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var namespace = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAMESPACE_ARGUMENT, String.class));
        var itemType = String.valueOf(ctx.getArgument(ModelPermissionCommand.CATEGORY_ARGUMENT, String.class));
        var itemName = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAME_ARGUMENT, String.class));

        var nsc = new NamespaceCategory(namespace, itemType);
        return Result.err(new Reason.NotAnError(nsc.withItemName(itemName)));
    }
}

