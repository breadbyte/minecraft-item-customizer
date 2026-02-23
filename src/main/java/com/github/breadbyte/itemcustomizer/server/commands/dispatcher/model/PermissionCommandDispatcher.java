package com.github.breadbyte.itemcustomizer.server.commands.dispatcher.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.operations.model.PermissionOperations;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class PermissionCommandDispatcher {
    public static int getPermissionNode(CommandContext<ServerCommandSource> ctx) {
        return PreOperations.executeOperation(ctx, PermissionOperations::GetPermissionNode, StackRequirement.NONE,"");
    }

    public static int grantModelPerm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return PreOperations.executeOperation(ctx, PermissionOperations::GrantPermission, StackRequirement.NONE,"Permission granted!");
    }

    public static int revokeModelPerm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return PreOperations.executeOperation(ctx, PermissionOperations::RevokePermission, StackRequirement.NONE,"Permission revoked!");
    }
}
