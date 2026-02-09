package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.operations.RenameOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class RenameCommands {

    public static int renameItem(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var input = String.valueOf(ctx.getArgument("name", String.class));
        var res = RenameOperations.renameItem(player, input);
        if (res.ok()) {
            Helper.SendMessageYes(player, res.details());
            Helper.ApplyCost(player, res.cost());
        } else {
            Helper.SendMessageNo(player, res.details());
        }
        return 1;
    }

    public static int resetName(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var res = RenameOperations.resetName(player);
        if (res.ok()) {
            Helper.SendMessageYes(player, res.details());
            Helper.ApplyCost(player, res.cost());
        } else {
            Helper.SendMessageNo(player, res.details());
        }
        return 1;
    }
}

