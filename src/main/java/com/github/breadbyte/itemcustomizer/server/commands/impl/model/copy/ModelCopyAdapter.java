package com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.ModelCopyParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelCopyCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
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

        var itemComps = mainHand.getComponents();
        if (itemComps.get(DataComponentTypes.LOCK) != null) {
            return Result.err(new Reason.InternalError("Main hand item is locked!"));
        }

        var offHandComps = offHand.getComponents();
        if (offHandComps.get(DataComponentTypes.LOCK) != null) {
            return Result.err(new Reason.InternalError("Offhand item is locked!"));
        }

        try {
            var copyWhat = ctx.getArgument(ModelCopyCommand.COPY_WHAT_ARGUMENT, String.class);
            var copyTo = ctx.getArgument(ModelCopyCommand.COPY_TO_ARGUMENT, String.class);

            if (copyWhat == null || copyTo == null)
                return Result.ok(new ModelCopyParams(player, mainHand, offHand, null, null));

            var copyWhatEnum = COPY_WHAT_ARGUMENT.valueOf(copyWhat);
            var copyToEnum = COPY_TO_ARGUMENT.valueOf(copyTo);

            return Result.ok(new ModelCopyParams(player, mainHand, offHand, copyToEnum, copyWhatEnum));
        } catch (IllegalArgumentException e) {
            return Result.err(new Reason.InternalError("Invalid Parameters"));
        }
    }
}
