package com.github.breadbyte.itemcustomizer.server.commands.registrar;

import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.loader.impl.lib.classtweaker.api.AccessWidener;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;

public class InternalHelper {

    public static LiteralArgumentBuilder<ServerCommandSource> RequirePermissionFor(LiteralArgumentBuilder<ServerCommandSource> root, Permission permission) {
        return root.requires(scs -> {
            if (Objects.isNull(scs)) return false;

            // The command builder runs the requires check before the command is fully registered,
            // so it doesn't have a player context yet.
            // If so, return true.
            if (Objects.isNull(scs.getPlayer())) return true;

            return AccessValidator.HasPermissionFor(permission, scs.getPlayer());
        });
    }
}
