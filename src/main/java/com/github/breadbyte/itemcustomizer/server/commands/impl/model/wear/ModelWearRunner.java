package com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear.IModelWearRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelWearRunner implements IModelWearRunner {

    ModelWearAdapter adapter;
    ModelWearOperations operations;

    public ModelWearRunner(ModelWearAdapter adapter, ModelWearOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int toggleWearable(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::apply, params, StackRequirement.REQUIRED_MAINHAND, "", 0);
    }
}
