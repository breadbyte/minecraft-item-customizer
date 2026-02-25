package com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelGlintRunner {
    int toggleGlint(CommandContext<ServerCommandSource> ctx);
}
