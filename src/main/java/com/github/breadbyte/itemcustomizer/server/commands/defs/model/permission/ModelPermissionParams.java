package com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission;

import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.core.jmx.Server;

public record ModelPermissionParams(NamespaceCategory namespace, ServerCommandSource cmdSrc, EntitySelector targetPlayer) {
}
