package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.PermissionCommand;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.Luckperms;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class GrantCommands {

    public static int grantModelPerm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var itemType = String.valueOf(ctx.getArgument(PermissionCommand.CATEGORY_ARGUMENT, String.class));
        var itemName = String.valueOf(ctx.getArgument(PermissionCommand.NAME_ARGUMENT, String.class));
        var playerArg = ctx.getArgument(PermissionCommand.PLAYER_ARGUMENT, EntitySelector.class);
        var cmdSrc = ctx.getSource();

        // TODO: should be able to be executed by console
        if (!cmdSrc.isExecutedByPlayer())
            return 0;

        var executor = cmdSrc.getPlayer();

        var targetPlayer = playerArg.getPlayer(cmdSrc);
        if (targetPlayer == null) {
            if (!Luckperms.IsLuckpermsPresent()) {
                Helper.SendError(cmdSrc, "Player must be online!");
                return 0;
            }
        }

        // TODO: Could probably make this a little better (extract to a function?)
        var splt = itemType.split("\\.");
        var model = ModelsIndex.INSTANCE.get(splt[0], splt[1], itemName);
        if (model != null) {

            if (targetPlayer == null) {
                targetPlayer = cmdSrc.getServer().getPlayerManager().getPlayer(playerArg.toString());
            }

            if (!Luckperms.IsLuckpermsPresent()) {
                Helper.SendError(cmdSrc, "LuckPerms is not installed on this server, cannot grant permission");
                Helper.SendError(cmdSrc, Check.Permission.CUSTOMIZE.chain(model.getPermissionNode()));
                return 0;
            }

            // If LuckPerms exists in the target server,
            // Directly access the LuckPerms API to add the permission node to the target player

            Luckperms.GrantPermission(targetPlayer, Check.Permission.CUSTOMIZE.chain(model.getPermissionNode()));

            Helper.SendMessage(cmdSrc, "Granted permission " + Check.Permission.CUSTOMIZE.chain(model.getPermissionNode()) + " to " + targetPlayer.getDisplayName().getString() + ".");
        }
        else {
            Helper.SendError(cmdSrc, "No model found for " + itemType + "." + itemName + ", please check the item type and name and try again.");
            return 0;
        }
        return 1;
    }

    public static int revokeModelPerm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var itemType = String.valueOf(ctx.getArgument(PermissionCommand.CATEGORY_ARGUMENT, String.class));
        var itemName = String.valueOf(ctx.getArgument(PermissionCommand.NAME_ARGUMENT, String.class));
        var playerArg = ctx.getArgument(PermissionCommand.PLAYER_ARGUMENT, EntitySelector.class);

        // TODO: should be able to be executed by console
        if (!ctx.getSource().isExecutedByPlayer())
            return 0;

        var executor = ctx.getSource().getPlayer();

        var targetPlayer = playerArg.getPlayer(ctx.getSource());
        if (targetPlayer == null) {
            if (!Luckperms.IsLuckpermsPresent()) {
                Helper.SendError(ctx.getSource(), "Player must be online!");
                return 0;
            }
        }

        // TODO: Could probably make this a little better (extract to a function?)
        var splt = itemType.split("\\.");
        var model = ModelsIndex.INSTANCE.get(splt[0], splt[1], itemName);
        if (model != null) {

            if (targetPlayer == null) {
                targetPlayer = ctx.getSource().getServer().getPlayerManager().getPlayer(playerArg.toString());
            }

            if (!Luckperms.IsLuckpermsPresent()) {
                Helper.SendError(ctx.getSource(), "LuckPerms is not installed on this server, cannot revoke permissions.");
                Helper.SendError(ctx.getSource(), Check.Permission.GRANT.chain(model.getPermissionNode()));
                return 0;
            }

            // If LuckPerms exists in the target server,
            // Directly access the LuckPerms API to add the permission node to the target player

            LuckPerms lpapi = LuckPermsProvider.get();
            var lpuser = lpapi.getPlayerAdapter(ServerPlayerEntity.class).getUser(targetPlayer);
            lpuser.data().remove(Node.builder(Check.Permission.CUSTOMIZE.chain(model.getPermissionNode())).build());
            lpapi.getUserManager().saveUser(lpuser);

            Helper.SendMessage(ctx.getSource(), "Revoked permission " + Check.Permission.CUSTOMIZE.chain(model.getPermissionNode()) + " to " + targetPlayer.getDisplayName().getString() + ".");
        }
        else {
            Helper.SendError(ctx.getSource(), "No model found for " + itemType + "." + itemName + ", please check the item type and name and try again.");
            return 0;
        }
        return 1;
    }
}
