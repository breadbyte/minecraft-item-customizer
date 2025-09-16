package com.github.breadbyte.itemcustomizer.server.command;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.Storage;
import com.github.breadbyte.itemcustomizer.server.operations.ModelOperations;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import static com.github.breadbyte.itemcustomizer.server.Check.TryReturnValidPlayer;

public class GrantCommands {

    public static int grantModelPerm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var itemType = String.valueOf(ctx.getArgument("item_type", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));
        var playerArg = ctx.getArgument("player", EntitySelector.class);

        // TODO: should be able to be executed by console
        if (!ctx.getSource().isExecutedByPlayer())
            return 0;

        var executor = ctx.getSource().getPlayer();

        var targetPlayer = playerArg.getPlayer(ctx.getSource());
        if (targetPlayer == null) {
            if (!Check.IsLuckpermsPresent()) {
                Helper.SendMessageNo(ctx.getSource().getPlayer(), "Permissions can only be granted to offline players using LuckPerms.");
                Helper.SendMessageNo(ctx.getSource().getPlayer(), "Please try again when the target player is online.");
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

            if (!Check.IsLuckpermsPresent()) {
                Helper.SendMessageNo(executor, "LuckPerms is not installed on this server, cannot grant permissions.");
                Helper.SendMessageNo(executor, "Please refer to your permissions plugin documentation on how to grant permissions for the following node:");
                Helper.SendMessageNo(executor, Check.Permission.GRANT.chain(model.getPermissionNode()));
                return 0;
            }

            // If LuckPerms exists in the target server,
            // Directly access the LuckPerms API to add the permission node to the target player

            LuckPerms lpapi = LuckPermsProvider.get();
            var lpuser = lpapi.getPlayerAdapter(ServerPlayerEntity.class).getUser(targetPlayer);
            lpuser.data().add(Node.builder(Check.Permission.CUSTOMIZE.chain(model.getPermissionNode())).build());
            lpapi.getUserManager().saveUser(lpuser);

            Helper.SendMessageYes(executor, "Granted permission " + Check.Permission.CUSTOMIZE.chain(model.getPermissionNode()) + " to " + targetPlayer.getDisplayName().getString() + ".");
        }
        else {
            Helper.SendMessageNo(executor, "No model found for " + itemType + "." + itemName + ", please check the item type and name and try again.");
            return 0;
        }
        return 1;
    }

    public static int revokeModelPerm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var itemType = String.valueOf(ctx.getArgument("item_type", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));
        var playerArg = ctx.getArgument("player", EntitySelector.class);

        // TODO: should be able to be executed by console
        if (!ctx.getSource().isExecutedByPlayer())
            return 0;

        var executor = ctx.getSource().getPlayer();

        var targetPlayer = playerArg.getPlayer(ctx.getSource());
        if (targetPlayer == null) {
            if (!Check.IsLuckpermsPresent()) {
                Helper.SendMessageNo(ctx.getSource().getPlayer(), "Permissions can only be revoked to offline players using LuckPerms.");
                Helper.SendMessageNo(ctx.getSource().getPlayer(), "Please try again when the target player is online.");
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

            if (!Check.IsLuckpermsPresent()) {
                Helper.SendMessageNo(executor, "LuckPerms is not installed on this server, cannot revoke permissions.");
                Helper.SendMessageNo(executor, "Please refer to your permissions plugin documentation on how to revoke permissions for the following node:");
                Helper.SendMessageNo(executor, Check.Permission.GRANT.chain(model.getPermissionNode()));
                return 0;
            }

            // If LuckPerms exists in the target server,
            // Directly access the LuckPerms API to add the permission node to the target player

            LuckPerms lpapi = LuckPermsProvider.get();
            var lpuser = lpapi.getPlayerAdapter(ServerPlayerEntity.class).getUser(targetPlayer);
            lpuser.data().remove(Node.builder(Check.Permission.CUSTOMIZE.chain(model.getPermissionNode())).build());
            lpapi.getUserManager().saveUser(lpuser);

            Helper.SendMessageYes(executor, "Revoked permission " + Check.Permission.CUSTOMIZE.chain(model.getPermissionNode()) + " to " + targetPlayer.getDisplayName().getString() + ".");
        }
        else {
            Helper.SendMessageNo(executor, "No model found for " + itemType + "." + itemName + ", please check the item type and name and try again.");
            return 0;
        }
        return 1;
    }
}
