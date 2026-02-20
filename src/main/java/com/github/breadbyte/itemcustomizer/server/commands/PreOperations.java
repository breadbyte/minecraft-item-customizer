package com.github.breadbyte.itemcustomizer.server.commands;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.text.Text.literal;

public class PreOperations {
    public static ServerPlayerEntity ValidateState(CommandContext<ServerCommandSource> context, int cost) {
        var playerContainer = TryReturnValidPlayer(context, Check.Permission.CUSTOMIZE.getPermission());

        if (!ValidateCost(Objects.requireNonNull(context.getSource().getPlayer()), cost)) {
            return null;
        }

        return playerContainer.orElse(null);
    }

    public static boolean ValidateCost(ServerPlayerEntity player, int cost) {
        if (player.isCreative())
            return true;

        if (player.experienceLevel < cost) {
            Helper.SendMessageNo(player, "This command requires at least " + cost + " experience level(s)!");
            return false;
        }

        return true;
    }


    public static void ApplyCost(ServerPlayerEntity player, int cost) {
        if (cost == 0)
            return;

        if (ValidateCost(player, cost))
            player.setExperienceLevel(player.experienceLevel - cost);
    }

    public static Optional<ServerPlayerEntity> TryReturnValidPlayer(CommandContext<ServerCommandSource> context, String PermissionName) {
        if (context.getSource().getPlayer() == null) {
            context.getSource().sendFeedback(() -> literal("Command can only be called by a player."), false);
            return Optional.empty();
        }

        var player = context.getSource().getPlayer();

        // Check for permission (Redundant since we check it in the command registration, but better safe than sorry)
        if (!Permissions.check(player, PermissionName)) {
            if (!Check.IsAdmin(player)) {
                Helper.SendMessageNo(player, "You do not have permission to use this command!");
                return Optional.empty();
            }
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
