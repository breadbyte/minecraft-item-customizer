package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.LoreCommand;
import com.github.breadbyte.itemcustomizer.server.operations.LoreOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class LoreCommandsPreChecked {

    public static int addLore(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.LORE.toString());
        var ctxSrc = ctx.getSource();

        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctxSrc);
            return 0;
        }

        var input = String.valueOf(ctx.getArgument(LoreCommand.LORE_ARGUMENT, String.class));
        var res = LoreOperations.addLore(player.unwrap(), input);
        if (res.ok()) {
            Helper.SendMessage(ctxSrc, res.details());
            PreOperations.TryApplyCost(player.unwrap(), res.cost());
        } else {
            Helper.SendError(ctxSrc, res.details());
        }
        return 1;
    }

    public static int resetLore(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.LORE.toString());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var res = LoreOperations.resetLore(player.unwrap());
        if (res.ok()) {
            Helper.SendMessage(ctx.getSource(), res.details());
            PreOperations.TryApplyCost(player.unwrap(), res.cost());
        } else {
            Helper.SendError(ctx.getSource(), res.details());
        }
        return 1;
    }
}

