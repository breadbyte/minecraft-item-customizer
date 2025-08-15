package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

    /**
     * Checks if the player has the required permission for the operation.
     *
     * @param player     The player to check permissions for.
     * @param permission The permission to check.
     * @return True if the player has the permission, false otherwise.
     */
    static boolean HasPermissionFor(PlayerEntity player, Check.Permission permission) {

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            return player.isCreative();
        }

        return Permissions.check(player, permission.getPermission());
    }

    static Optional<ServerPlayerEntity> TryReturnValidState(CommandContext<ServerCommandSource> context, String PermissionName) {
        if (context.getSource().getPlayer() == null) {
            context.getSource().sendFeedback(() -> literal("Command can only be called by a player."), false);
            return Optional.empty();
        }

        var player = context.getSource().getPlayer();

        if (!Permissions.check(player, PermissionName)) {
            Helper.SendMessageNo(player, "You do not have permission to use this command!");
        }

        var playerItem = player.getMainHandStack();
        if (playerItem == ItemStack.EMPTY) {
            player.sendMessage(of("You are not holding an item!"), true);
            return Optional.empty();
        }

        return Optional.of(player);
    }
}
