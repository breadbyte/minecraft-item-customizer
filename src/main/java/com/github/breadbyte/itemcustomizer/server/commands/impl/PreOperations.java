package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PreOperations {

    public enum ERROR {
        INVALID_PLAYER,
        NO_PERMISSION,
        NO_ITEM,
        NO_EXP,
        WRONG_OWNERSHIP;

        @Override
        public String toString() {
            return switch (this) {
                case INVALID_PLAYER -> "Command can only be called by a player.";
                case NO_PERMISSION -> "You do not have permission to use this command!";
                case NO_ITEM -> "You are not holding an item!";
                case NO_EXP -> "This command requires at least 1 experience level!";
                case WRONG_OWNERSHIP -> "You do not own this item!";
            };
        }

        public void BroadcastToPlayer(ServerCommandSource src) {
            Helper.SendError(src, toString());
        }
    }
    public enum CostResult {
        SUCCESS,
        SUCCESS_CREATIVE
    }

    public static Result<CostResult, ERROR> ValidateCost(ServerPlayerEntity player, int cost) {
        if (player.isCreative())
            return Result.ok(CostResult.SUCCESS_CREATIVE);

        if (player.experienceLevel < cost) {
            return Result.err(ERROR.NO_EXP);
        }

        return Result.ok(CostResult.SUCCESS);
    }

    public static Result<ServerPlayerEntity, ERROR> TryReturnValidPlayer(CommandContext<ServerCommandSource> context, String PermissionName) {
        // Is the player a null object?
        if (Objects.isNull(context.getSource().getPlayer())) {
            return Result.err(ERROR.INVALID_PLAYER);
        }

        var player = context.getSource().getPlayer();

        // Check for permission (Redundant since we check it in the command registration, but better safe than sorry)
        if (!AccessValidator.HasPermissionFor(PermissionName, player)) {
            return Result.err(ERROR.NO_PERMISSION);
        }

        // Check if the player has something in their hand
        if (Objects.isNull(TryGetValidPlayerCurrentHand(player))) {
            return Result.err(ERROR.NO_ITEM);
        }

        return Result.ok(player);
    }

    public static Result<CostResult, ERROR> TryApplyCost(ServerPlayerEntity player, int cost) {
        var validate = ValidateCost(player, cost);
        if (validate.isErr()) {
            return validate;
        }

        player.setExperienceLevel(player.experienceLevel - cost);
        return validate;
    }

    public static Result<ServerPlayerEntity, ERROR> ValidateStack(CommandContext<ServerCommandSource> ctx, int cost, String permissionNode) {
        var validatePlayer = TryReturnValidPlayer(ctx, permissionNode);
        if (validatePlayer.isErr()) {
            return validatePlayer;
        }

        var validateOwnership = AccessValidator.IsModelOwner(validatePlayer.unwrap());
        if (!validateOwnership) {
            return Result.err(ERROR.WRONG_OWNERSHIP);
        }

        var validateCost = ValidateCost(validatePlayer.unwrap(), cost);
        if (validateCost.isErr()) {
            return Result.err(validateCost.unwrapErr());
        }

        return Result.ok(validatePlayer.unwrap());
    }

    public static Result<ItemStack, ERROR> TryGetValidPlayerCurrentHand(ServerPlayerEntity player) {
        if (player == null) {
            return Result.err(ERROR.INVALID_PLAYER);
        }

        var stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            return Result.err(ERROR.NO_ITEM);
        }

        return Result.ok(stack);
    }
}
