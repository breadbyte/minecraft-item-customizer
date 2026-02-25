package com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore.IModelLoreRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelLoreRunner implements IModelLoreRunner {
    private final ModelLoreAdapter adapter;
    private final ModelLoreOperations operations;

    public ModelLoreRunner(ModelLoreAdapter adapter, ModelLoreOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int applyLore(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::apply, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }

    @Override
    public int resetLore(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::reset, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }
}
