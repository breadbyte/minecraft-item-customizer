package com.github.breadbyte.itemcustomizer.main;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Helper {
    enum Permission {
        CUSTOMIZE("itemcustomizer.customize"),
        RENAME("itemcustomizer.rename"),
        LORE("itemcustomizer.lore");

        private final String permission;

        Permission(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }
    }

    static ServerPlayerEntity performChecks(CommandContext<ServerCommandSource> context, String PermissionName) {
        if (context.getSource().getPlayer() == null) {
            context.getSource().sendFeedback(() -> Text.literal("Command can only be called by a player."), false);
            return null;
        }

        var player = context.getSource().getPlayer();

        LuckPerms api = LuckPermsProvider.get();
        var user = api.getUserManager().loadUser(context.getSource().getPlayer().getUuid());
        try {
            var perm = user.get().getCachedData().getPermissionData().checkPermission(PermissionName).asBoolean();
            if (!perm) {
                player.sendMessage(Text.of("You do not have permission to use this command!"), true);
                player.playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            context.getSource().sendFeedback(() -> Text.literal("An error occurred while checking permissions. Check the console for more information."), false);
            ItemCustomizer.LOGGER.error("An error occurred while checking permissions.", e);
        }

        var playerItem = player.getMainHandStack();
        if (playerItem == ItemStack.EMPTY) {
            player.sendMessage(Text.of("You are not holding an item!"), true);
            return null;
        }

        return player;
    }

    static void ApplyCost(ServerPlayerEntity player, int cost) {
        if (!player.isCreative()) {
            if (player.experienceLevel < cost) {
                player.sendMessage(Text.of("This command requires at least " + cost + " experience level(s)!"), true);
                player.playSound(SoundEvents.BLOCK_ANVIL_LAND);
                return;
            }
        }
        player.setExperienceLevel(player.experienceLevel - cost);
    }

    static void SendMessage(ServerPlayerEntity player, String message, @Nullable SoundEvent sound) {
        player.sendMessage(Text.of(message), true);
        if (sound != null) {
            player.playSoundToPlayer(sound, SoundCategory.MASTER, 1.0f, 1.0f);
        } else {
            player.playSoundToPlayer(SoundEvents.ITEM_TOTEM_USE, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }

    static void SendMessage(ServerPlayerEntity player, Text message, @Nullable SoundEvent sound) {
        player.sendMessage(message, true);
        if (sound != null) {
            player.playSoundToPlayer(sound, SoundCategory.MASTER, 1.0f, 1.0f);
        } else {
            player.playSoundToPlayer(SoundEvents.ITEM_TOTEM_USE, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }

    static int Debug(CommandContext<ServerCommandSource> context) {
        var player = performChecks(context, Permission.CUSTOMIZE.getPermission());
        var playerItem = player.getMainHandStack();

        // Get the currently applied lore in the item
        var ttd = playerItem.getTooltipData();
        player.sendMessage(Text.of(String.valueOf(ttd)), true);

        var arrayL = new ArrayList<Text>();

        //arrayL.add(Text.literal("Test").setStyle(new Style()));
        var LoreC = new LoreComponent(arrayL);

        playerItem.set(DataComponentTypes.LORE, LoreC);
        return 1;
    }

    static String ToSomewhatValidJson(String input) {
        // Check if we're a plain string. If we are, escape it.
        if (input.charAt(0) != '[' && input.charAt(input.length() - 1) != ']') {
            // Escape the string
            String escapedString = '"' + input + '"';

            // Escape backslashes
            escapedString = escapedString.replace("\\", "\\\\");
            return escapedString;
        }

        // Return otherwise
        return input;
    }

    static Text JsonString2Text(String input) {
        Gson gson = new Gson();
        String pseudojson = ToSomewhatValidJson(input);
        return TextCodecs.CODEC
                .decode(JsonOps.INSTANCE, gson.fromJson(pseudojson, JsonElement.class))
                .getOrThrow()
                .getFirst();
    }

    static Identifier String2Identifier(String namespace, String path) {
        return Identifier.of(namespace, path);
    }
}
