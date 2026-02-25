package com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelApplyRunner {
    int applyModel(CommandContext<ServerCommandSource> ctx);
}
