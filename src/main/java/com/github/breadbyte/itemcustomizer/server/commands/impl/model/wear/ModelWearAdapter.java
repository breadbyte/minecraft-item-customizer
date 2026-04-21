package com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.ModelWearParams;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.ModelWearSlot;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelNamespaceCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelWearCommand;
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

        ModelWearSlot paramSlot = null;
        try {
            String rawParam = ctx.getArgument(ModelWearCommand.SLOT_ARGUMENT, String.class);
            switch (rawParam) {
                case "chest": paramSlot = ModelWearSlot.CHEST; break;
                case "legs": paramSlot = ModelWearSlot.LEGS; break;
                case "feet": paramSlot = ModelWearSlot.FEET; break;
                default: paramSlot = ModelWearSlot.HEAD; break;
            }
        } catch (IllegalArgumentException ignored) {
        }

        return Result.ok(new ModelWearParams(playerItem.unwrap(), paramSlot));
    }
}
