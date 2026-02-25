package com.github.breadbyte.itemcustomizer.server.commands.defs.model.equipment;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface IModelEquipmentRunner {
    int setEquipmentTexture(CommandContext<ServerCommandSource> ctx);
    int resetEquipmentTexture(CommandContext<ServerCommandSource> ctx);
}
