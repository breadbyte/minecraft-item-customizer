package com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.ModelLockParams;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore.ModelLoreParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.LoreCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelLoreAdapter implements Adapter<ModelLoreParams> {
    @Override
    public Result<ModelLoreParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var hand = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (hand.isErr()) return Result.err(hand.unwrapErr());

        var input = String.valueOf(ctx.getArgument(LoreCommand.LORE_ARGUMENT, String.class));
        return Result.ok(new ModelLoreParams(hand.unwrap(), input));
    }
}
