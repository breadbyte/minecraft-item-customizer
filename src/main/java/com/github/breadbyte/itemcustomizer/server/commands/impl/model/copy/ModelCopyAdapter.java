package com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.ModelCopyParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelCopyAdapter implements Adapter<ModelCopyParams> {
    @Override
    public Result<ModelCopyParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var playerResult = PreOperations.TryReturnValidPlayer(ctx);
        if (playerResult.isErr()) return Result.err(playerResult.unwrapErr());
        var player = playerResult.unwrap();

        var mainHand = player.getMainHandStack();
        var offHand = player.getOffHandStack();

        if (mainHand.isEmpty() || offHand.isEmpty()) {
            return Result.err(Reason.NO_ITEM);
        }

        return Result.ok(new ModelCopyParams(mainHand, offHand));
    }
}
