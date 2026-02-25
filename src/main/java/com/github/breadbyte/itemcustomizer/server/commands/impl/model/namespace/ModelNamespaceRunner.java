package com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace.IModelNamespaceRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelNamespaceRunner implements IModelNamespaceRunner {

    ModelNamespaceAdapter adapter;
    ModelNamespaceOperations operations;

    public ModelNamespaceRunner(ModelNamespaceAdapter adapter, ModelNamespaceOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int addNamespace(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::addNamespace, params, StackRequirement.NONE, "", 1);
    }

    @Override
    public int removeNamespace(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::removeNamespace, params, StackRequirement.NONE, "", 1);
    }

    @Override
    public int clearAll(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::clearAll, params, StackRequirement.NONE, "", 1);
    }
}
