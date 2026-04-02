package com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelCopyRunner {
    int copyOffhandToMainhand(CommandContext<ServerCommandSource> ctx);
}
