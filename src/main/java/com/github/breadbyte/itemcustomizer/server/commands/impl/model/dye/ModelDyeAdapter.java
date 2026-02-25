package com.github.breadbyte.itemcustomizer.server.commands.impl.model.dye;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye.ModelDyeParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelDyeCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelDyeAdapter implements Adapter<ModelDyeParams> {
    @Override
    public Result<ModelDyeParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var colorClass = ctx.getArgument(ModelDyeCommand.COLOR_ARGUMENT, Integer.class);

        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var itemStack = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (itemStack.isErr()) return Result.err(itemStack.unwrapErr());

        return Result.ok(new ModelDyeParams(itemStack.unwrap(), colorClass));
    }
}
