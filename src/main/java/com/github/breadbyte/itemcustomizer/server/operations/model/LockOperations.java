package com.github.breadbyte.itemcustomizer.server.operations.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;

public class LockOperations {
    public static Result<Void> lockModel(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var playerHand = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (playerHand.isErr()) {
            return Result.err(playerHand.unwrapErr());
        }

        // Get the components for the currently held item
        var playerItem = playerHand.unwrap();
        var itemComps = playerItem.getComponents();
        var playerUuid = Text.literal(player.getUuidAsString());

        if (itemComps.get(DataComponentTypes.LOCK) != null) {
            var lock = playerItem.getComponents().get(DataComponentTypes.LOCK);
            var pred = lock.predicate().components().exact();

            // Convert to ComponentChanges to get typed access
            ComponentChanges changes = pred.toChanges();

            // Read the specific component value
            Optional<? extends Text> name = changes.get(DataComponentTypes.CUSTOM_NAME);
            if (name == null) {
                return Result.err(new Reason.InternalError("Failed to read lock component"));
            }

            if (name.isPresent()) {
                String uuid = name.get().getString();
                if (!uuid.equals(player.getUuidAsString())) {
                    try {
                        var locker = player.getEntityWorld().getServer().getPlayerManager().getPlayer(uuid).getName();
                        return Result.err(new Reason.InternalError("This item is locked by + " + locker + " and cannot be modified!"));
                    } catch (NullPointerException e) {
                        return Result.err(new Reason.InternalError("This item is locked by another player and cannot be modified!"));
                    }
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

    public static Result<Void> unlockModel(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var playerHand = PreOperations.TryGetValidPlayerCurrentHand(player);
        var playerItem = playerHand.unwrap();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();
        var playerUuid = Text.literal(player.getUuidAsString());

        var lock = itemComps.get(DataComponentTypes.LOCK);

        if (lock == null)
            return Result.err(new Reason.InternalError("Item is not locked!"));

        var pred = lock.predicate().components().exact();

        // Convert to ComponentChanges to get typed access
        ComponentChanges changes = pred.toChanges();

        // Read the specific component value
        Optional<? extends Text> name = changes.get(DataComponentTypes.CUSTOM_NAME);
        if (name == null) {
            return Result.err(new Reason.InternalError("Failed to read lock component"));
        }

        if (name.isPresent()) {
            String uuid = name.get().getString();
            if (!uuid.equals(player.getUuidAsString())) {
                return Result.err(new Reason.InternalError("This item is locked by another player and cannot be modified!"));
            }
        }

        return Result.ok();
    }
}
