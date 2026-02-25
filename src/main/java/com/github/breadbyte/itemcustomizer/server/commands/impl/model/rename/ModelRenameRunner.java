package com.github.breadbyte.itemcustomizer.server.commands.impl.model.rename;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.rename.IModelRenameRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelRenameRunner implements IModelRenameRunner {

    private final ModelRenameAdapter adapter;
    private final ModelRenameOperations operations;

    public ModelRenameRunner(ModelRenameAdapter adapter, ModelRenameOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int renameItem(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::apply, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }

    @Override
    public int resetName(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::reset, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }
}
