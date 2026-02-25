package com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.equipment.ModelEquipmentParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelEquipmentAdapter implements Adapter<ModelEquipmentParams> {

    @Override
    public Result<ModelEquipmentParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var playerHand = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (playerHand.isErr()) return Result.err(playerHand.unwrapErr());

        return Result.ok(new ModelEquipmentParams(playerHand.unwrap()));
    }
}
