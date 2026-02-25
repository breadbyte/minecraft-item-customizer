package com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelPermissionRunner {
    int grantPermission(CommandContext<ServerCommandSource> ctx);
    int revokePermission(CommandContext<ServerCommandSource> ctx);
    int getPermissionNode(CommandContext<ServerCommandSource> ctx);
}
