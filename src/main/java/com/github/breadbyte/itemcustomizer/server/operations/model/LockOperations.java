package com.github.breadbyte.itemcustomizer.server.operations.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

public class LockOperations {
    public static Result<Void> lockModel(PlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var playerHand = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (playerHand.isErr()) {
            return Result.err(playerHand.unwrapErr());
        }

        // Get the components for the currently held item
        var playerItem = playerHand.unwrap();
        var itemComps = playerItem.getComponents();
        var playerUuid = Text.literal(player.getUuidAsString());

        if (itemComps.get(DataComponentTypes.LOCK) != null) {
            var lock = ReadLockComponent(player);

            if (lock.isErr()) {
                return Result.err(lock.unwrapErr());
            }

            if (!lock.unwrap().equals(player.getUuidAsString())) {
                try {
                    var locker = player.getEntityWorld().getServer().getPlayerManager().getPlayer(lock.unwrap()).getName();
                    return Result.err(new Reason.InternalError("This item is locked by + " + locker + " and cannot be modified!"));
                } catch (NullPointerException e) {
                    return Result.err(new Reason.InternalError("This item is locked by another player and cannot be modified!"));
                }
            }
        } else {
            // Set it to the new model
            playerItem.set(DataComponentTypes.LOCK, new ContainerLock(new ItemPredicate.Builder()
                    .components(ComponentsPredicate.Builder.create()
                            .exact(ComponentMapPredicate.of(DataComponentTypes.CUSTOM_NAME, playerUuid))
                            .build())
                    .build()));
        }

        return Result.ok();
    }

    public static Result<Void> unlockModel(PlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var playerHand = PreOperations.TryGetValidPlayerCurrentHand(player);
        var playerItem = playerHand.unwrap();

        var readLock = ReadLockComponent(player);
        if (readLock.isErr()) {
            return Result.err(readLock.unwrapErr());
        }

        if (!readLock.unwrap().equals(player.getUuidAsString())) {
            return Result.err(new Reason.InternalError("This item is locked by another player and cannot be modified!"));
        }

        playerItem.remove(DataComponentTypes.LOCK);
        return Result.ok();
    }

    static Result<String> ReadLockComponent(PlayerEntity player) {
        var hand = PreOperations.TryGetValidPlayerCurrentHand(player);
        var playerItem = hand.unwrap();
        var itemComps = playerItem.getComponents();

        if (itemComps.get(DataComponentTypes.LOCK) == null) {
            return Result.ok("This item is not locked!");
        }

        var lock = itemComps.get(DataComponentTypes.LOCK);

        var pred = lock.predicate().components().exact();

        // Convert to ComponentChanges to get typed access
        ComponentChanges changes = pred.toChanges();

        // Read the specific component value
        Optional<? extends Text> name = changes.get(DataComponentTypes.CUSTOM_NAME);
        if (name == null) {
            return Result.err(new Reason.InternalError("Failed to read lock component"));
        }

        if (name.isPresent()) {
            return Result.ok(name.get().getString());
        } else {
            return Result.ok("This item is locked, but the locker could not be identified!");
        }
    }
}
