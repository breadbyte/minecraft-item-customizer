package com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.IModelLockOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.ModelLockParams;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.runner.StackRequirement;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;

import java.util.Optional;

public class ModelLockOperations implements IModelLockOperations {
    @Override
    public Result<String> lock(ModelLockParams params) {
        // Get the components for the currently held item
        var playerItem = params.item();
        var itemComps = playerItem.getComponents();
        var playerUuid = Text.literal(params.uuid());

        if (itemComps.get(DataComponentTypes.LOCK) != null) {
            var lock = ReadLockComponent(playerItem);

            if (lock.isErr()) {
                return Result.err(lock.unwrapErr());
            }

            if (!lock.unwrap().equals(params.uuid())) {
                try {
                    // Holder should not be null here
                    assert params.item().getHolder() != null;

                    var locker = params.item().getHolder().getEntityWorld().getServer().getPlayerManager().getPlayer(lock.unwrap()).getName();
                    return Result.err(new Reason.InternalError("This item is locked by + " + locker + " and cannot be modified!"));
                } catch (NullPointerException e) {
                    return Result.err(new Reason.InternalError("This item is locked by another player and cannot be modified!"));
                }
            }
        } else {
            // Set it to the new model
            playerItem.set(DataComponentTypes.LOCK, new ContainerLock(new ItemPredicate.Builder()
                    .components(ComponentsPredicate.Builder.create()
                            .exact(ComponentMapPredicate.of(DataComponentTypes.ITEM_NAME, playerUuid))
                            .build())
                    .build()));
        }

        return Result.ok();
    }

    @Override
    public Result<String> unlock(ModelLockParams params) {
        var playerItem = params.item();

        var readLock = ReadLockComponent(playerItem);
        if (readLock.isErr()) {
            return Result.err(readLock.unwrapErr());
        }

        if (!readLock.unwrap().equals(params.uuid())) {
            if (!readLock.unwrap().contains("-"))
                return Result.err(new Reason.InternalError("This item is locked to a group and cannot be modified!"));

            return Result.err(new Reason.InternalError("This item is locked by another player and cannot be modified!"));
        }

        playerItem.remove(DataComponentTypes.LOCK);
        return Result.ok();
    }

    public static Result<String> ReadLockComponent(ItemStack item) {
        var itemComps = item.getComponents();

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
