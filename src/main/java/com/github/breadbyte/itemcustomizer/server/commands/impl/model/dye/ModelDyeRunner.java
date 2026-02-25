package com.github.breadbyte.itemcustomizer.server.commands.impl.model.dye;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye.IModelDyeOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye.IModelDyeRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelDyeRunner implements IModelDyeRunner {

    public ModelDyeRunner(ModelDyeAdapter adapter, IModelDyeOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    ModelDyeAdapter adapter;
    IModelDyeOperations operations;

    @Override
    public int applyDye(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::apply, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }

    @Override
    public int resetDye(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::reset, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }
}
