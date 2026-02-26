package com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.equipment.IModelEquipmentRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelEquipmentRunner implements IModelEquipmentRunner {

    ModelEquipmentAdapter adapter;
    ModelEquipmentOperations operations;

    public ModelEquipmentRunner(ModelEquipmentAdapter adapter, ModelEquipmentOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int setEquipmentTexture(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::toggle, params, StackRequirement.REQUIRED_MAINHAND, "", 0);
    }

    @Override
    public int resetEquipmentTexture(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::reset, params, StackRequirement.REQUIRED_MAINHAND, "", 0);
    }
}
