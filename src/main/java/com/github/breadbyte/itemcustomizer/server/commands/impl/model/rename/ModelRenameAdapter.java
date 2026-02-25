package com.github.breadbyte.itemcustomizer.server.commands.impl.model.rename;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.rename.ModelRenameParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelRenameAdapter implements Adapter<ModelRenameParams> {
    @Override
    public Result<ModelRenameParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var item = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (item.isErr()) return Result.err(item.unwrapErr());

        var input = String.valueOf(ctx.getArgument(RenameCommand.RENAME_ARGUMENT, String.class));
        return Result.ok(new ModelRenameParams(item.unwrap(), input));
    }
}
