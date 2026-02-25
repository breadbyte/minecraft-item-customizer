package com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.IModelLockRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelLockRunner implements IModelLockRunner {

    private final ModelLockAdapter adapter;
    private final ModelLockOperations operations;

    public ModelLockRunner(ModelLockAdapter adapter, ModelLockOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int lockModel(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::lock, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }

    @Override
    public int unlockModel(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::unlock, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }
}
