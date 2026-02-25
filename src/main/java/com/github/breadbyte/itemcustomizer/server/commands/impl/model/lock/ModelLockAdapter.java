package com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.ModelLockParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelLockAdapter implements Adapter<ModelLockParams> {
    @Override
    public Result<ModelLockParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var hand = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (hand.isErr()) return Result.err(hand.unwrapErr());

        return Result.ok(new ModelLockParams(hand.unwrap(), player.unwrap().getUuidAsString()));
    }
}
