package com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission.IModelPermissionRunner;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelPermissionRunner implements IModelPermissionRunner {

    ModelPermissionAdapter adapter;
    ModelPermissionOperations operations;

    public ModelPermissionRunner(ModelPermissionAdapter adapter, ModelPermissionOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    @Override
    public int grantPermission(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::grantPermission, params, StackRequirement.NONE, "Permission granted!", 0);
    }

    @Override
    public int revokePermission(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::revokePermission, params, StackRequirement.NONE, "Permission revoked!", 0);
    }

    @Override
    public int getPermissionNode(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::getPermissionNode, params, StackRequirement.NONE, "", 0);
    }
}
