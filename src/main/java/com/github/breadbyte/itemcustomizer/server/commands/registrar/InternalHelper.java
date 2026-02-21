package com.github.breadbyte.itemcustomizer.server.commands.registrar;

import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;

public class InternalHelper {

    public static LiteralArgumentBuilder<ServerCommandSource> RequirePermissionFor(LiteralArgumentBuilder<ServerCommandSource> root, Check.Permission permission) {
        return root.requires(scs -> {
            if (Objects.isNull(scs)) return false;

            // The command builder runs the requires check before the command is fully registered,
            // so it doesn't have a player context yet.
            // If so, return true.
            if (Objects.isNull(scs.getPlayer())) return true;

            // Check if we're a player
            if (scs.isExecutedByPlayer()) {

                // Check if we're op
                return scs.getServer().getPlayerManager().isOperator(scs.getPlayer().getPlayerConfigEntry());
            }

            return AccessValidator.HasPermissionFor(permission.getPermission(), scs.getPlayer());
        });
    }
}
