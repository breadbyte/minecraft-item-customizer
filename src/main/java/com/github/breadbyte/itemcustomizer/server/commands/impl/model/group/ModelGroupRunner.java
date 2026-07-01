package com.github.breadbyte.itemcustomizer.server.commands.impl.model.group;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint.IModelGlintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.group.IModelGroupOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.group.IModelGroupRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint.ModelGlintAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelGroupRunner implements IModelGroupRunner {

    public ModelGroupRunner(ModelGroupAdapter adapter, IModelGroupOperations operations) {
        this.adapter = adapter;
        this.operations = operations;
    }

    ModelGroupAdapter adapter;
    IModelGroupOperations operations;

    @Override
    public int AddGroup(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::addGroup, params, StackRequirement.NONE, "Added new group!", 0);
    }

    @Override
    public int RemoveGroup(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::removeGroup, params, StackRequirement.NONE, "Removed existing group!", 0);
    }

    @Override
    public int ListGroup(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::listGroup, params, StackRequirement.NONE, "", 0);
    }

    @Override
    public int PromoteAdmin(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::promoteAdmin, params, StackRequirement.NONE, "Promoted player to group admin!", 0);
    }

    @Override
    public int DemoteAdmin(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::demoteAdmin, params, StackRequirement.NONE, "Promoted player to group admin!", 0);
    }

    @Override
    public int AddToGroup(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::addToGroup, params, StackRequirement.NONE, "Added player to group!", 0);
    }

    @Override
    public int RemoveFromGroup(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::removeFromGroup, params, StackRequirement.NONE, "Removed player from group!", 0);
    }

    @Override
    public int LockToGroup(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::lockToGroup, params, StackRequirement.REQUIRED_MAINHAND, "Locked item to group!", 0);
    }

    @Override
    public int UnlockFromGroup(CommandContext<ServerCommandSource> ctx) {
        var params = adapter.getParams(ctx);
        return PreOperations.executeOperation(ctx, operations::unlockFromGroup, params, StackRequirement.REQUIRED_MAINHAND, "Unlocked item from group!", 0);
    }
}
