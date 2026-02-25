package com.github.breadbyte.itemcustomizer.server.commands.defs;

import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public interface Adapter<T> {
    Result<T> getParams(CommandContext<ServerCommandSource> ctx);
}
