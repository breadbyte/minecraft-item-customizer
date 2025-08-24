package com.github.breadbyte.itemcustomizer.server.data;

import net.minecraft.sound.SoundEvent;

public record OperationResult(boolean ok, String details, SoundEvent soundEvent, int cost) {

    public static OperationResult ok(String s, SoundEvent soundEvent) {
        return new OperationResult(true, s, soundEvent, 0);
    }

    public static OperationResult ok(String s, SoundEvent soundEffect, int cost) {
        return new OperationResult(true, s, soundEffect, cost);
    }

    public static OperationResult fail(String details) {
        return new OperationResult(false, details, null, 0);
    }

    public static OperationResult fail(String details, SoundEvent soundEffect) {
        return new OperationResult(false, details, soundEffect, 0);
    }
}
