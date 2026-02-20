package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.Storage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Objects;

import static com.github.breadbyte.itemcustomizer.server.commands.PreOperations.ValidateCost;

public class Helper {

    public static String ToSomewhatValidJson(String input) {
        // Return the input as-is if it is already valid JSON
        if (IsValidJson(input))
            return input;

        // Treat as plain string: wrap and escape backslashes
        return "\"" + input.replace("\\", "\\\\") + "\"";
    }

    public static Text JsonString2Text(String input) {
        Gson gson = new Gson();
        String pseudojson = ToSomewhatValidJson(input);
        return TextCodecs.CODEC
                .decode(JsonOps.INSTANCE, gson.fromJson(pseudojson, JsonElement.class))
                .getOrThrow()
                .getFirst();
    }

    public static boolean IsValidJson(String input) {
        // Accept either a JSON array or object boundary (fail-fast if neither)
        // technically "valid" json
        boolean hasValidBrackets =
                (input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']') ||
                (input.charAt(0) == '{' && input.charAt(input.length() - 1) == '}');

        if (!hasValidBrackets) return false;

        try {
            new Gson().fromJson(input, JsonElement.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Identifier String2Identifier(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    public static void tryLoadStorage() {
        if (!Storage.HANDLER.load()) {
            throw new IllegalStateException("Failed to load storage handler.");
        }
    }

    public static void SendMessage(ServerPlayerEntity player, String message, net.minecraft.sound.SoundEvent sound) {
        player.sendMessage(Text.of(message), true);
        if (sound != null) {
            player.getEntityWorld().playSoundClient(player.getX(), player.getY(), player.getZ(), sound, SoundCategory.MASTER, 1.0F, 1.0F, false);
        }
    }

    public static void SendMessage(ServerPlayerEntity player, Text message, net.minecraft.sound.SoundEvent sound) {
        player.sendMessage(message, true);
        if (sound != null) {
            player.getEntityWorld().playSoundClient(player.getX(), player.getY(), player.getZ(), sound, SoundCategory.MASTER, 1.0F, 1.0F, false);
        }
    }

    // Some events are apparently wrapped in a Reference
    public static void SendMessage(ServerPlayerEntity player, String suggestionsUpdated, RegistryEntry.Reference<SoundEvent> soundEventReference) {
        player.sendMessage(Text.of(suggestionsUpdated), true);
        player.getEntityWorld().playSoundClient(player.getX(), player.getY(), player.getZ(), soundEventReference.value(), SoundCategory.MASTER, 1.0F, 1.0F, false);
    }

    public static void SendMessageNo(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.of(message), true);
        player.getEntityWorld().playSoundClient(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0F, 1.0F, false);
    }

    public static void SendMessageYes(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.of(message), true);
        player.getEntityWorld().playSoundClient(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0F, 1.0F, false);
    }
}
