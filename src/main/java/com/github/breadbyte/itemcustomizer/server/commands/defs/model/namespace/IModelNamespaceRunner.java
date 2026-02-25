package com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelNamespaceRunner {
    int addNamespace(CommandContext<ServerCommandSource> ctx);
    int removeNamespace(CommandContext<ServerCommandSource> ctx);
    int clearAll(CommandContext<ServerCommandSource> ctx);
}
