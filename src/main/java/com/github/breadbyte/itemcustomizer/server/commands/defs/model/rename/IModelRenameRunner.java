package com.github.breadbyte.itemcustomizer.server.commands.defs.model.rename;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelRenameRunner {
    int renameItem(CommandContext<ServerCommandSource> ctx);
    int resetName(CommandContext<ServerCommandSource> ctx);
}
