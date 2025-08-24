package com.github.breadbyte.itemcustomizer.server;

import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

import static net.minecraft.text.Text.*;

public class Check {
    public enum Permission {
        CUSTOMIZE("itemcustomizer.customize"),
        RENAME("itemcustomizer.rename"),
        LORE("itemcustomizer.lore");

        private final String permission;

        Permission(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }
    }

    public static Optional<ServerPlayerEntity> TryReturnValidPlayer(CommandContext<ServerCommandSource> context, String PermissionName) {
        if (context.getSource().getPlayer() == null) {
            context.getSource().sendFeedback(() -> literal("Command can only be called by a player."), false);
            return Optional.empty();
        }

        var player = context.getSource().getPlayer();

        // Check for permission (Redundant since we check it in the command registration, but better safe than sorry)
        if (!Permissions.check(player, PermissionName)) {
            Helper.SendMessageNo(player, "You do not have permission to use this command!");
        }

        // Check if the player has something in their hand
        var playerItem = player.getMainHandStack();
        if (playerItem == ItemStack.EMPTY) {
            Helper.SendMessageNo(player,"You are not holding an item!");
            return Optional.empty();
        }

        // Check if the player has more than one experience level
        // (skips this check if the player is in creative mode)
        if (!player.isCreative()) {
            if (player.experienceLevel < 1) {
                Helper.SendMessageNo(player, "This command requires at least 1 experience level!");
                return Optional.empty();
            }
        }

        return Optional.of(player);
    }
}
