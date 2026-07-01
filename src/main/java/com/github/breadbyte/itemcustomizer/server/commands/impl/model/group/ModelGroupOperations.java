package com.github.breadbyte.itemcustomizer.server.commands.impl.model.group;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.group.IModelGroupOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.group.ModelGroupParams;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock.ModelLockOperations;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.*;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.types.InheritanceNode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ModelGroupOperations implements IModelGroupOperations {
    @Override
    public Result<String> addGroup(ModelGroupParams params) {
        if (LuckPermsProvider.get().getGroupManager().getGroup(params.groupName()) == null) {
            LuckPermsProvider.get().getGroupManager().createAndLoadGroup(params.groupName());
            return Result.ok("Group " + params.groupName() + " successfully created!");
        }
        else return Result.ok("Group already exists!");
    }

    @Override
    public Result<String> removeGroup(ModelGroupParams params) {
        if (LuckPermsProvider.get().getGroupManager().getGroup(params.groupName()) == null) {
            return Result.ok("Group " + params.groupName() + " does not exist");
        }
        else {
            LuckPermsProvider.get().getGroupManager().deleteGroup(LuckPermsProvider.get().getGroupManager().getGroup(params.groupName()));
            return Result.ok("Group " + params.groupName() + " successfully removed!");
        }
    }

    @Override
    public Result<String> listGroup(ModelGroupParams params) {
        var groups = LuckPermsProvider.get().getGroupManager().getLoadedGroups();
        for (var group : groups) {
            Postmaster.Chat_SendMessage_Yes(params.sourcePlayer().getCommandSource(), MutableText.of(Text.literal(group.getName()).getContent()));
        }

        return Result.ok("Please refer to chat for the list of possible groups");
    }

    @Override
    public Result<String> promoteAdmin(ModelGroupParams params) {
        // admins have permission itemcustomizer.groups.admin.group
        Luckperms.GrantPermission(params.targetPlayer(), Permission.GROUP.chain("admin", params.groupName()).toString());
        return Result.ok("Promoted player " + params.targetPlayer().getName().getString() + " to group admin for group " + params.groupName());
    }

    @Override
    public Result<String> demoteAdmin(ModelGroupParams params) {
        Luckperms.RevokePermission(params.targetPlayer(), Permission.GROUP.chain("admin", params.groupName()));
        return Result.ok("Demoted player " + params.targetPlayer().getName().getString() + " from group admin for group " + params.groupName());
    }

    @Override
    public Result<String> addToGroup(ModelGroupParams params) {
        if (!Luckperms.CheckPermission(params.targetPlayer(), Permission.GROUP.chain("admin", params.groupName()))){
            if (!AccessValidator.IsAdmin(params.sourcePlayer())) {
                return Result.ok("You do not have permission to add players to this group!");
            }
        }

        if (LuckPermsProvider.get().getGroupManager().getGroup(params.groupName()) == null) {
            LuckPermsProvider.get().getGroupManager().createAndLoadGroup(params.groupName());
        }

        LuckPermsProvider.get().getUserManager().modifyUser(params.targetPlayer().getUuid(), user -> {
            InheritanceNode node = InheritanceNode.builder(params.groupName()).build();
            user.data().add(node);
        });

        return Result.ok("Added player " + params.targetPlayer().getName().getString() + " to group " + params.groupName());
    }

    @Override
    public Result<String> removeFromGroup(ModelGroupParams params) {
        if (!Luckperms.CheckPermission(params.targetPlayer(), Permission.GROUP.chain("admin", params.groupName()))) {
            if (!AccessValidator.IsAdmin(params.sourcePlayer())) {
                return Result.ok("You do not have permission to remove players from this group!");
            }
        }

        LuckPermsProvider.get().getUserManager().modifyUser(params.targetPlayer().getUuid(), user -> {
            InheritanceNode node = InheritanceNode.builder(params.groupName()).build();
            user.data().remove(node);
        });

        return Result.ok("Removed player " + params.targetPlayer().getName().getString() + " from group " + params.groupName());
    }

    @Override
    public Result<String> lockToGroup(ModelGroupParams params) {
        var mhs = params.sourcePlayer().getMainHandStack();
        if (!Luckperms.CheckPermission(params.targetPlayer(), Permission.GROUP.chain("admin", params.groupName()))) {
            if (!AccessValidator.IsAdmin(params.sourcePlayer())) {
                return Result.ok("You do not have permission to lock items to this group!");
            }
        }

        if (LuckPermsProvider.get().getGroupManager().getGroup(params.groupName()) == null) {
            return Result.ok("This group does not exist!");
        }

        if (mhs.get(DataComponentTypes.LOCK) != null) {
            return Result.ok("This item is already locked!");
        }

        mhs.set(DataComponentTypes.LOCK, new ContainerLock(new ItemPredicate.Builder()
                .components(ComponentsPredicate.Builder.create()
                        .exact(ComponentMapPredicate.of(DataComponentTypes.ITEM_NAME, Text.literal(params.groupName())))
                        .build())
                .build()));

        return Result.ok("Locked item to group " + params.groupName());
    }

    @Override
    public Result<String> unlockFromGroup(ModelGroupParams params) {
        var mhs = params.sourcePlayer().getMainHandStack();
        var lock = mhs.get(DataComponentTypes.LOCK);
        var component = ModelLockOperations.ReadLockComponent(mhs);

        if (!Luckperms.CheckPermission(params.targetPlayer(), Permission.GROUP.chain("admin", params.groupName()))) {
            if (!AccessValidator.IsAdmin(params.sourcePlayer())) {
                return Result.ok("You do not have permission to unlock items from this group!");
            }
        }

        if (LuckPermsProvider.get().getGroupManager().getGroup(params.groupName()) == null) {
            return Result.ok("This group does not exist!");
        }

        if (lock != null) {
            if (component.isErr()) return Result.err(component.unwrapErr());
            if (component.unwrap().contains("-")) return Result.ok("This item is locked!");
            if (!component.unwrap().equalsIgnoreCase(params.groupName())) return Result.ok("This item is not locked to this group!");
        }

        mhs.remove(DataComponentTypes.LOCK);
        return Result.ok("Unlocked item from group " + params.groupName());
    }
}
