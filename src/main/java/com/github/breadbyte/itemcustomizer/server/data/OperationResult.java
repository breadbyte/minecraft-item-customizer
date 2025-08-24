package com.github.breadbyte.itemcustomizer.server.data;

import net.minecraft.sound.SoundEvent;

public final class OperationResult<T> {
    private final boolean ok;
    private final String details;
    private final SoundEvent soundEvent;
    private final int cost;

    private OperationResult(boolean ok, String details, SoundEvent soundEvent, int cost) {
        this.ok = ok;
        this.details = details;
        this.soundEvent = soundEvent;
        this.cost = cost;
    }

    public static OperationResult ok(String s, SoundEvent soundEvent) { return new OperationResult(true, s, soundEvent, 0); }

    public static OperationResult ok(String s, SoundEvent soundEffect, int cost) { return new OperationResult(true, s, soundEffect, cost); }

    public static OperationResult fail(String details) { return new OperationResult(false, details, null, 0); }

    public static OperationResult fail(String details, SoundEvent soundEffect) { return new OperationResult(false, details, soundEffect, 0); }

    public boolean isOk() { return ok; }
    public String details() { return details; }
    public int getCost() { return cost; }
    public SoundEvent getSoundEvent() { return soundEvent; }
}
