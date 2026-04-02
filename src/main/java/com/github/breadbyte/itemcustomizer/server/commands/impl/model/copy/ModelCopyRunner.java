package com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.IModelCopyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.IModelCopyRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelCopyRunner implements IModelCopyRunner {

    private final ModelCopyAdapter adapter;
    private final IModelCopyOperations operations;

    public ModelCopyRunner(ModelCopyAdapter adapter, IModelCopyOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int copyOffhandToMainhand(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::copy, params, StackRequirement.REQUIRED_MAINHAND, "Copied offhand properties!", 1);
    }
}
