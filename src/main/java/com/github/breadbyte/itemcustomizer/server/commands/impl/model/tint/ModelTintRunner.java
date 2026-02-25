package com.github.breadbyte.itemcustomizer.server.commands.impl.model.tint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.tint.IModelTintRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelTintRunner implements IModelTintRunner {

    private final ModelTintAdapter adapter;
    private final ModelTintOperations operations;

    public ModelTintRunner(ModelTintAdapter adapter, ModelTintOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int applyTint(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::apply, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }

    @Override
    public int resetTint(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::reset, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }
}
