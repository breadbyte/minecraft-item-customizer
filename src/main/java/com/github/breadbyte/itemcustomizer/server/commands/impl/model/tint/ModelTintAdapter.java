package com.github.breadbyte.itemcustomizer.server.commands.impl.model.tint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.tint.ModelTintParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelTintCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelTintAdapter implements Adapter<ModelTintParams> {
    @Override
    public Result<ModelTintParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var index = ctx.getArgument(ModelTintCommand.TINT_INDEX_ARGUMENT, Integer.class);
        var color = ctx.getArgument(ModelTintCommand.TINT_COLOR_ARGUMENT, Integer.class);

        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var item = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (item.isErr()) return Result.err(item.unwrapErr());

        return Result.ok(new ModelTintParams(item.unwrap(), index, color));
    }
}
