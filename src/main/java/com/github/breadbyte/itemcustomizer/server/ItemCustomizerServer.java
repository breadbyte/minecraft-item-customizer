package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.MinecraftVersion;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Identifier;

public class ItemCustomizerServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        // Load existing definitions, if available
        Helper.tryLoadStorage();

        // Initialize the cache
        ModelsIndex inst = ModelsIndex.getInstance();
        inst.initialize();

    }
}
