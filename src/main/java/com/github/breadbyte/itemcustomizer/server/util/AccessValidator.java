package com.github.breadbyte.itemcustomizer.server.util;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.Optional;

public class AccessValidator {
    public static boolean HasPermissionFor(String permission, ServerPlayerEntity player)
    {
        // Allow all functions if we're running on a singleplayer logical server
        if (player.getEntityWorld().getServer().isSingleplayer())
            return true;

        if (IsAdmin(player)) return true;
        if (Luckperms.IsLuckpermsPresent())
            return Luckperms.CheckPermission(player, permission);

        return false;
    }

    public static boolean IsAdmin(ServerPlayerEntity player) {
        var server = Objects.requireNonNull(player.getEntityWorld().getServer());
        return server.getPlayerManager().isOperator(player.getPlayerConfigEntry());
    }

    public static boolean IsModelOwner(ServerPlayerEntity player) {
        if (player == null) {
            return false;
        }

        var playerItem = player.getMainHandStack();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();
        var playerUuid = player.getUuidAsString();

        if (playerItem.isEmpty()) {
            return false;
        }

        if (itemComps.get(DataComponentTypes.LOCK) != null) {
            var lock = playerItem.getComponents().get(DataComponentTypes.LOCK);
            var pred = lock.predicate().components().exact();

            // Convert to ComponentChanges to get typed access
            ComponentChanges changes = pred.toChanges();

            // Read the specific component value
            Optional<? extends Text> name = changes.get(DataComponentTypes.CUSTOM_NAME);
            if (name == null) {
                return false;
            }

            if (name.isPresent()) {
                String uuid = name.get().getString();
                return uuid.equals(playerUuid);
            }
        }

        // True by default (no lock)
        return true;
    }
}
