package com.github.breadbyte.itemcustomizer.server.commands.registrar;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;

public class InternalHelper {

    public static LiteralArgumentBuilder<ServerCommandSource> RequirePermissionFor(LiteralArgumentBuilder<ServerCommandSource> root, Check.Permission permission) {
        return root.requires(scs -> {
            // Initial permissions check so it shows up on autocomplete
            if (!Permissions.check(scs, permission.getPermission()))
                return false;

            // So we don't have permission, are we a player?
            if (scs.isExecutedByPlayer()) {

                // Check if we're op (the guard is for the getPlayer call)
                return scs.getServer().getPlayerManager().isOperator(Objects.requireNonNull(scs.getPlayer()).getPlayerConfigEntry());
            }

            // False by default
            return false;
        });
    }
}
