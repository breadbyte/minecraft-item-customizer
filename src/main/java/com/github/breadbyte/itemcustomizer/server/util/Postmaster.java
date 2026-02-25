package com.github.breadbyte.itemcustomizer.server.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static net.minecraft.text.Text.literal;

public class Postmaster {

    static final SoundEvent SOUND_YES = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    static final SoundEvent SOUND_NO = SoundEvents.ENTITY_VILLAGER_NO;
    static void PlaySoundYes(PlayerEntity player) {
        if (player == null) return;
        player.getEntityWorld().playSoundClient(player.getX(), player.getY(), player.getZ(), SOUND_YES, SoundCategory.MASTER, 1.0F, 1.0F, false);
    }
    static void PlaySoundNo(PlayerEntity player) {
        if (player == null) return;
        player.getEntityWorld().playSoundClient(player.getX(), player.getY(), player.getZ(), SOUND_NO, SoundCategory.MASTER, 1.0F, 1.0F, false);
    }

    public static void Hud_SendMessage_Yes(ServerCommandSource src, String message) {
        var player = src.getPlayer();
        if (player == null) return;

        player.sendMessage(Text.of(message), true);
        PlaySoundYes(player);
    }

    public static void Hud_SendMessage_No(ServerCommandSource src, String message) {
        var player = src.getPlayer();
        if (player == null) return;

        player.sendMessage(Text.of(message), true);
        PlaySoundNo(player);
    }

    public static void Chat_SendMessage_Yes(ServerCommandSource src, MutableText textObj) {
        var player = src.getPlayer();
        if (player == null) return;

        src.sendFeedback(() -> textObj, false);
        PlaySoundYes(player);
    }

    public static void Chat_SendMessage_No(ServerCommandSource src, String message) {
        var player = src.getPlayer();
        if (player == null) return;

        src.sendFeedback(() -> Text.literal(message), false);
        PlaySoundNo(player);
    }

    public static void Chat_SendError(ServerCommandSource src, String message) {
        src.sendError(literal(message));
    }

    // Used for interactive error messages
    public static void Chat_SendError_No(ServerCommandSource src, MutableText message) {
        src.sendError(message);
        PlaySoundNo(src.getPlayer());
    }

    static Boolean NullCheck(PlayerEntity p) { return p != null; }
}
