package com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission;

import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;

public record ModelPermissionParams(ModelPath namespace, ServerCommandSource cmdSrc, EntitySelector targetPlayer) {
}
