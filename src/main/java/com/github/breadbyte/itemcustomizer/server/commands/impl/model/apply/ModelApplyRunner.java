package com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.IModelApplyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.IModelApplyRunner;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.ModelApplyParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.util.Postmaster;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelApplyRunner implements IModelApplyRunner {

    public ModelApplyRunner(ModelApplyAdapter adapter, IModelApplyOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    ModelApplyAdapter adapter;
    IModelApplyOperations operations;

    @Override
    public int applyModel(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        if (params.isErr()) {
            Postmaster.Hud_SendMessage_No(ctx.getSource(), params.unwrapErr().getMessage());
            return 0;
        }

        return PreOperations.executeOperation(ctx, operations::apply, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }

    public int resetModel(CommandContext<ServerCommandSource> ctx) {
        // HACK HACK: GetParams should handle partial params for commands!!
        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) {
            Postmaster.Hud_SendMessage_No(ctx.getSource(), player.unwrapErr().getMessage());
            return 0;
        }
        var params = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (params.isErr()) {
            Postmaster.Hud_SendMessage_No(ctx.getSource(), player.unwrapErr().getMessage());
            return 0;
        }

        var params1 = Result.ok(new ModelApplyParams(params.unwrap(), null, null));
        return PreOperations.executeOperation(ctx, operations::reset, params1, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }
}
