package com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.IModelApplyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.IModelGlintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.IModelGlintRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply.ModelApplyAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.util.Postmaster;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelGlintRunner implements IModelGlintRunner {

    public ModelGlintRunner(ModelGlintAdapter adapter, IModelGlintOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    ModelGlintAdapter adapter;
    IModelGlintOperations operations;

    @Override
    public int toggleGlint(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::toggle, params, StackRequirement.REQUIRED_MAINHAND, "", 1);
    }
}
