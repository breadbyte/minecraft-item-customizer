package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.operations.LoreOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class LoreCommandsPreChecked {

    public static int addLore(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var input = String.valueOf(ctx.getArgument("text", String.class));
        var res = LoreOperations.addLore(player, input);
        if (res.ok()) {
            Helper.SendMessageYes(player, res.details());
            Helper.ApplyCost(player, res.cost());
        } else {
            Helper.SendMessageNo(player, res.details());
        }
        return 1;
    }

    public static int resetLore(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var res = LoreOperations.resetLore(player);
        if (res.ok()) {
            Helper.SendMessageYes(player, res.details());
            Helper.ApplyCost(player, res.cost());
        } else {
            Helper.SendMessageNo(player, res.details());
        }
        return 1;
    }
}

