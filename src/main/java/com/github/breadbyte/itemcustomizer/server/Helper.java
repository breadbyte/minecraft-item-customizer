package com.github.breadbyte.itemcustomizer.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

import static com.github.breadbyte.itemcustomizer.server.Check.TryReturnValidState;

public class Helper {

    static void ApplyCost(ServerPlayerEntity player, int cost) {
        if (!player.isCreative()) {
            if (player.experienceLevel < cost) {
                player.sendMessage(Text.of("This command requires at least " + cost + " experience level(s)!"), true);

                return;
            }
        }
        player.setExperienceLevel(player.experienceLevel - cost);
    }
//
//    static int Debug(CommandContext<ServerCommandSource> context) {
//        var player = TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
//        var playerItem = player.getMainHandStack();
//
//        // Get the currently applied lore in the item
//        var ttd = playerItem.getTooltipData();
//        player.sendMessage(Text.of(String.valueOf(ttd)), true);
//
//        var arrayL = new ArrayList<Text>();
//
//        //arrayL.add(Text.literal("Test").setStyle(new Style()));
//        var LoreC = new LoreComponent(arrayL);
//
//        playerItem.set(DataComponentTypes.LORE, LoreC);
//        return 1;
//    }

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

    static void SendMessage(ServerPlayerEntity player, String message, net.minecraft.sound.SoundEvent sound) {
        player.sendMessage(Text.of(message), true);
        if (sound != null) {
            player.playSound(sound, 1.0F, 1.0F);
        }
    }

    static void SendMessage(ServerPlayerEntity player, Text message, net.minecraft.sound.SoundEvent sound) {
        player.sendMessage(message, true);
        if (sound != null) {
            player.playSound(sound, 1.0F, 1.0F);
        }
    }

    static void SendMessageNo(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.of(message), true);
        player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
    }
}
