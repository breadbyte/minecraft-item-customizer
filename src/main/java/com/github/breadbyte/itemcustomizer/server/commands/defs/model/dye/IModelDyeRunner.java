package com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelDyeRunner {
    int applyDye(CommandContext<ServerCommandSource> ctx);
    int resetDye(CommandContext<ServerCommandSource> ctx);
}
