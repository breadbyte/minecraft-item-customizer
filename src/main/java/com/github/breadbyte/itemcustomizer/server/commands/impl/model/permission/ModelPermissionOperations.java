package com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission.IModelPermissionOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission.ModelPermissionParams;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class ModelPermissionOperations implements IModelPermissionOperations {
    @Override
    public Result<String> grantPermission(ModelPermissionParams params) {
        ServerPlayerEntity targetPlayer = null;
        try {
            targetPlayer = params.targetPlayer().getPlayer(params.cmdSrc());
        } catch (CommandSyntaxException e) {
            return Result.err(new Reason.InternalError("Player must be online!"));
        }

        ModelPath ns = params.namespace();
        if (Luckperms.IsLuckpermsPresent()) {
            // Grant anyway if we're admin, otherwise don't
            if (!AccessValidator.IsAdmin(Objects.requireNonNull(params.cmdSrc().getPlayer()))) {
                // Does this model exist? Check if we aren't admin
                var model = ModelsIndex.INSTANCE.getExact(params.namespace());
                if (model.isErr()) return Result.err(new Reason.InternalError("Invalid model, please check your command or send an error report!"));
            }

            Luckperms.GrantPermission(targetPlayer, Permission.CUSTOMIZE.chain(ns.getPermissionNode()).getPermission());
            return Result.ok();
        }
        else {
            return Result.err(new Reason.InternalError("LuckPerms not present, grant manually instead: " + Permission.CUSTOMIZE.chain(ns.getPermissionNode()).getPermission()));
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

        ModelPath ns = params.namespace();
        if (Luckperms.IsLuckpermsPresent()) {
            // Grant anyway if we're admin, otherwise don't
            if (!AccessValidator.IsAdmin(Objects.requireNonNull(params.cmdSrc().getPlayer()))) {
                // Does this model exist? Check if we aren't admin
                var model = ModelsIndex.INSTANCE.getExact(params.namespace());
                if (model.isErr()) return Result.err(new Reason.InternalError("Invalid model, please check your command or send an error report!"));
            }

            Luckperms.RevokePermission(targetPlayer, Permission.CUSTOMIZE.chain(ns.getPermissionNode()));
            return Result.ok();
        }
        else {
            return Result.err(new Reason.InternalError("LuckPerms not present, revoke manually instead: " + Permission.CUSTOMIZE.chain(ns.getPermissionNode()).getPermission()));
        }
    }

    @Override
    public Result<String> getPermissionNode(ModelPermissionParams params) {
        var nsc = params.namespace();
        return Result.err(new Reason.NotAnError(nsc.getPermissionNode()));
    }
}
