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
    public int copyAll(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::copyAll, params, StackRequirement.REQUIRED_MAINHAND, "Copied all properties!", 1);
    }

    @Override
    public int copyName(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::copyName, params, StackRequirement.REQUIRED_MAINHAND, "Copied name!", 1);
    }

    @Override
    public int copyLore(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::copyLore, params, StackRequirement.REQUIRED_MAINHAND, "Copied lore!", 1);
    }

    @Override
    public int copyModel(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::copyModel, params, StackRequirement.REQUIRED_MAINHAND, "Copied model properties!", 1);
    }
}
