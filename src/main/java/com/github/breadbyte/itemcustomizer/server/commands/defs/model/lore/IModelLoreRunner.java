package com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelLoreRunner {
    int applyLore(CommandContext<ServerCommandSource> ctx);
    int resetLore(CommandContext<ServerCommandSource> ctx);
}
