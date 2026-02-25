package com.github.breadbyte.itemcustomizer.server.commands.runner;

import com.github.breadbyte.itemcustomizer.server.util.*;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;

import java.util.Objects;

public class PreOperations {

    public static <T> int executeOperation(
            CommandContext<ServerCommandSource> ctx,
            Executor<T> operation,
            Result<T> paramsResult,
            StackRequirement orientation,
            String successMessage,
            Integer operationCost) {

        var ctxSrc = ctx.getSource();
        var player = PreOperations.ValidateStack(ctx, 1);

        if (player.isErr()) {
            Postmaster.Hud_SendMessage_No(ctxSrc, player.unwrapErr().getMessage());
            return 0;
        }

        if (paramsResult.isErr()) {
            Postmaster.Chat_SendError(ctxSrc, paramsResult.unwrapErr().getMessage());
            return 0;
        }
        var params = paramsResult.unwrap();

        switch (orientation) {
            case NONE:
                break;
            case SPECIAL_UNLOCK:
            case REQUIRED_MAINHAND:
            case REQUIRED_OFFHAND:
            default: {
                var hand = TryGetValidPlayerCurrentHand(player.unwrap());

                if (hand.isErr()) {
                    Postmaster.Hud_SendMessage_No(ctxSrc, hand.unwrapErr().getMessage());
                    return 0;
                }

                var validateOwnership = AccessValidator.IsModelOwner(player.unwrap());
                if (!validateOwnership) {
                    Postmaster.Hud_SendMessage_No(ctxSrc, Reason.WRONG_OWNERSHIP.getMessage());
                    return 0;
                }

                if (orientation != StackRequirement.SPECIAL_UNLOCK) {
                    if (AccessValidator.IsModelLocked(player.unwrap())) {
                        Postmaster.Hud_SendMessage_No(ctxSrc, Reason.ITEM_LOCKED_OWNER.getMessage());
                        return 0;
                    }
                }
            }
        }

        var retval = operation.run(params);

        if (retval.isOk()) {
            if (retval.unwrap() instanceof String message) {
                Postmaster.Hud_SendMessage_Yes(ctxSrc, message);
            }
            else { Postmaster.Hud_SendMessage_Yes(ctxSrc, successMessage); }
            PreOperations.TryApplyCost(player.unwrap(), operationCost);
        } else {
            Postmaster.Chat_SendError(ctxSrc, retval.unwrapErr().getMessage());
        }

        return 1;
    }

    private static Result<Void> ValidateCost(PlayerEntity player, int cost) {
        if (player.isCreative())
            return Result.ok();

        if (player.experienceLevel < cost) {
            return Result.err(new Reason.NoExp(player.experienceLevel, cost));
        }

        return Result.ok();
    }

    public static Result<PlayerEntity> TryReturnValidPlayer(CommandContext<ServerCommandSource> context) {
        // Is the player a null object?
        if (Objects.isNull(context.getSource().getPlayer())) {
            return Result.err(Reason.INVALID_PLAYER);
        }

        var player = context.getSource().getPlayer();
        return Result.ok(player);
    }

    public static Result<Void> TryApplyCost(PlayerEntity player, int cost) {
        var validate = ValidateCost(player, cost);
        if (validate.isErr()) {
            return validate;
        }

        player.addExperienceLevels(-cost);
        return validate;
    }

    // TODO: We shouldn't need to validate permission here since we're gated by Brigadier already?
    public static Result<PlayerEntity> ValidateStack(CommandContext<ServerCommandSource> ctx, int cost) {
        var validatePlayer = TryReturnValidPlayer(ctx);
        if (validatePlayer.isErr()) {
            return validatePlayer;
        }

        var validateCost = ValidateCost(validatePlayer.unwrap(), cost);
        if (validateCost.isErr()) {
            return Result.err(validateCost.unwrapErr());
        }

        return Result.ok(validatePlayer.unwrap());
    }

    public static Result<Void> ValidatePermission(CommandContext<ServerCommandSource> ctx, Permission permissionNode) {
        // Check for permission (Redundant since we check it in the command registration, but better safe than sorry)
        if (!AccessValidator.HasPermissionFor(permissionNode, TryReturnValidPlayer(ctx).unwrap())) {
            return Result.err(Reason.NO_PERMISSION);
        }

        return Result.ok();
    }

    public static Result<ItemStack> TryGetValidPlayerCurrentHand(PlayerEntity player) {
        var stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            return Result.err(Reason.NO_ITEM);
        }

        return Result.ok(stack);
    }
}
