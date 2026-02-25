package com.github.breadbyte.itemcustomizer.server.commands.defs.model.tint;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelTintRunner {
    int applyTint(CommandContext<ServerCommandSource> ctx);
    int resetTint(CommandContext<ServerCommandSource> ctx);
}
