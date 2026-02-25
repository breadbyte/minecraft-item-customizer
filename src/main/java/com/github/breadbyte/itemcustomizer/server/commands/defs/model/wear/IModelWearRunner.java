package com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelWearRunner {
    int toggleWearable(CommandContext<ServerCommandSource> ctx);
}
