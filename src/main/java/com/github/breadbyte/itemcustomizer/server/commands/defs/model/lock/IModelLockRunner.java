package com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelLockRunner {
    int lockModel(CommandContext<ServerCommandSource> ctx);
    int unlockModel(CommandContext<ServerCommandSource> ctx);
}
