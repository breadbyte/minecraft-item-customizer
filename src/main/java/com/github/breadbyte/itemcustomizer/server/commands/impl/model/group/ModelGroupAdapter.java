package com.github.breadbyte.itemcustomizer.server.commands.impl.model.group;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.group.ModelGroupParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelGroupCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModelGroupAdapter implements Adapter<ModelGroupParams> {
    @Override
    public Result<ModelGroupParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var getPlayer = PreOperations.TryReturnValidPlayer(ctx);
        if (getPlayer.isErr()) return Result.err(getPlayer.unwrapErr());

        var groupName = ctx.getArgument(ModelGroupCommand.GROUP_NAME_ARGUMENT, String.class);
        var playerTarget = ctx.getArgument(ModelGroupCommand.PLAYER_NAME_ARGUMENT, EntitySelector.class);
        var player = getPlayer.unwrap();
        ServerPlayerEntity target;

        try {
            target = playerTarget.getPlayer(ctx.getSource());
        }
        catch (Exception e) {
            return Result.err(new Reason.InternalError("Player must be online!"));
        }

        return Result.ok(new ModelGroupParams(groupName, (ServerPlayerEntity)player, target));
    }
}
