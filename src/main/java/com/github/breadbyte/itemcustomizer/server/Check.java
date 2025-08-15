package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
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
        //Luckperms doesn't exist on the client side, so we can skip the permission check

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            return player.isCreative();
        }

        try {
            LuckPerms api = LuckPermsProvider.get();
            var user = api.getUserManager().loadUser(player.getUuid());
            return user.get().getCachedData().getPermissionData().checkPermission(permission.getPermission()).asBoolean();
        } catch (InterruptedException | ExecutionException e) {
            ItemCustomizer.LOGGER.error("An error occurred while checking permissions.", e);
            return false;
        }
    }

    static Optional<ServerPlayerEntity> TryReturnValidState(CommandContext<ServerCommandSource> context, String PermissionName) {
        if (context.getSource().getPlayer() == null) {
            context.getSource().sendFeedback(() -> literal("Command can only be called by a player."), false);
            return Optional.empty();
        }

        var player = context.getSource().getPlayer();

        LuckPerms api = LuckPermsProvider.get();
        var user = api.getUserManager().loadUser(context.getSource().getPlayer().getUuid());
        try {
            var perm = user.get().getCachedData().getPermissionData().checkPermission(PermissionName).asBoolean();
            if (!perm) {
                Helper.SendMessageNo(player, "You do not have permission to use this command!");
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            context.getSource().sendFeedback(() -> literal("An error occurred while checking permissions. Check the console for more information."), false);
            ItemCustomizer.LOGGER.error("An error occurred while checking permissions.", e);
        }

        var playerItem = player.getMainHandStack();
        if (playerItem == ItemStack.EMPTY) {
            player.sendMessage(of("You are not holding an item!"), true);
            return Optional.empty();
        }

        return Optional.of(player);
    }
}
