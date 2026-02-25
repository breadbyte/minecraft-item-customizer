package com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.ModelGlintParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelGlintAdapter implements Adapter<ModelGlintParams> {
    @Override
    public Result<ModelGlintParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var getPlayer = PreOperations.TryReturnValidPlayer(ctx);
        if (getPlayer.isErr()) return Result.err(getPlayer.unwrapErr());

        var player = getPlayer.unwrap();

        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (getPlayerItem.isErr()) return Result.err(getPlayerItem.unwrapErr());

        return Result.ok(new ModelGlintParams(getPlayerItem.unwrap()));
    }
}
