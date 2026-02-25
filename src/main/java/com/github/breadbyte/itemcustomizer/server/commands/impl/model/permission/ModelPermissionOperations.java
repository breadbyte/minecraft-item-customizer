package com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission.IModelPermissionOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission.ModelPermissionParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelPermissionCommand;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.Luckperms;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModelPermissionOperations implements IModelPermissionOperations {
    @Override
    public Result<String> grantPermission(ModelPermissionParams params) {
        ServerPlayerEntity targetPlayer = null;
        try {
            targetPlayer = params.targetPlayer().getPlayer(params.cmdSrc());
        } catch (CommandSyntaxException e) {
            return Result.err(new Reason.InternalError("Player must be online!"));
        }

        NamespaceCategory ns = params.namespace();
        if (Luckperms.IsLuckpermsPresent()) {
            Luckperms.GrantPermission(targetPlayer, Permission.CUSTOMIZE.chain(ns.withItemNamePermissionNode(ns.itemName())).getPermission());
            return Result.ok();
        }
        else {
            return Result.err(new Reason.InternalError("LuckPerms not present, grant manually instead: " + Permission.CUSTOMIZE.chain(ns.withItemNamePermissionNode(ns.itemName())).getPermission()));
        }
    }

    @Override
    public Result<String> revokePermission(ModelPermissionParams params) {
        ServerPlayerEntity targetPlayer = null;
        try {
            targetPlayer = params.targetPlayer().getPlayer(params.cmdSrc());
        } catch (CommandSyntaxException e) {
            return Result.err(new Reason.InternalError("Player must be online!"));
        }

        NamespaceCategory ns = params.namespace();
        if (Luckperms.IsLuckpermsPresent()) {
            Luckperms.RevokePermission(targetPlayer, Permission.CUSTOMIZE.chain(ns.withItemNamePermissionNode(ns.itemName())));
            return Result.ok();
        }
        else {
            return Result.err(new Reason.InternalError("LuckPerms not present, revoke manually instead: " + Permission.CUSTOMIZE.chain(ns.withItemNamePermissionNode(ns.itemName())).getPermission()));
        }
    }

    @Override
    public Result<String> getPermissionNode(ModelPermissionParams params) {
        var nsc = params.namespace();
        return Result.err(new Reason.NotAnError(nsc.withItemNamePermissionNode(params.namespace().itemName())));
    }
}
