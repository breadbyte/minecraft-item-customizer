package com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelCopyRunner {
    int copyAll(CommandContext<ServerCommandSource> ctx);
    int copyName(CommandContext<ServerCommandSource> ctx);
    int copyLore(CommandContext<ServerCommandSource> ctx);
    int copyModel(CommandContext<ServerCommandSource> ctx);
}
