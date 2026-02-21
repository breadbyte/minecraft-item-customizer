package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.operations.RenameOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class RenameCommandsPreChecked {

    public static int renameItem(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var input = String.valueOf(ctx.getArgument(RenameCommand.RENAME_ARGUMENT, String.class));
        var res = RenameOperations.renameItem(player.unwrap(), input);
        if (res.ok()) {
            Helper.SendMessage(ctx.getSource(), res.details());
            PreOperations.TryApplyCost(player.unwrap(), res.cost());
        } else {
            Helper.SendError(ctx.getSource(), res.details());
        }
        return 1;
    }

    public static int resetName(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var res = RenameOperations.resetName(player.unwrap());
        if (res.ok()) {
            Helper.SendMessage(ctx.getSource(), res.details());
            PreOperations.TryApplyCost(player.unwrap(), res.cost());
        } else {
            Helper.SendError(ctx.getSource(), res.details());
        }
        return 1;
    }
}

