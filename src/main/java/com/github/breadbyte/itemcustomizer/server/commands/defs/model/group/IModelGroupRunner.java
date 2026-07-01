package com.github.breadbyte.itemcustomizer.server.commands.defs.model.group;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelGroupRunner {
    int AddGroup(CommandContext<ServerCommandSource> ctx);
    int RemoveGroup(CommandContext<ServerCommandSource> ctx);
    int ListGroup(CommandContext<ServerCommandSource> ctx);
    int PromoteAdmin(CommandContext<ServerCommandSource> ctx);
    int DemoteAdmin(CommandContext<ServerCommandSource> ctx);
    int AddToGroup(CommandContext<ServerCommandSource> ctx);
    int RemoveFromGroup(CommandContext<ServerCommandSource> ctx);
    int LockToGroup(CommandContext<ServerCommandSource> ctx);
    int UnlockFromGroup(CommandContext<ServerCommandSource> ctx);
}
