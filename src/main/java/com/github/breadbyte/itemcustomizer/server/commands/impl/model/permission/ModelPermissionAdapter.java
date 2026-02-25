package com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission.ModelPermissionParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelPermissionCommand;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;

public class ModelPermissionAdapter implements Adapter<ModelPermissionParams> {
    @Override
    public Result<ModelPermissionParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var namespace = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAMESPACE_ARGUMENT, String.class));
        var itemType = String.valueOf(ctx.getArgument(ModelPermissionCommand.CATEGORY_ARGUMENT, String.class));
        var itemName = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAME_ARGUMENT, String.class));
        var playerArg = ctx.getArgument(ModelPermissionCommand.PLAYER_ARGUMENT, EntitySelector.class);
        var cmdSrc = ctx.getSource();

        NamespaceCategory nc = new NamespaceCategory(namespace, itemType, itemName);
        return Result.ok(new ModelPermissionParams(nc, cmdSrc, playerArg));
    }
}
