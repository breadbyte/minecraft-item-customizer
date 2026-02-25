package com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.ModelWearParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelWearAdapter implements Adapter<ModelWearParams> {
    @Override
    public Result<ModelWearParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var playerItem = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (playerItem.isErr()) return Result.err(playerItem.unwrapErr());

        return Result.ok(new ModelWearParams(playerItem.unwrap()));
    }
}
