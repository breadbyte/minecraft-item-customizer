package com.github.breadbyte.itemcustomizer.main;

import com.github.breadbyte.itemcustomizer.server.CommandRegistration;
import com.github.breadbyte.itemcustomizer.server.EventRegistrar;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemCustomizer implements ModInitializer {

    // Setup Fabric Logger
    public static final String MOD_ID = "itemcustomizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Item Customizer starting up! - registering commands once.");

        // This allows the mod to register commands in both the integrated server and dedicated server environments, but not on the client.
        CommandRegistration.RegisterCommands();

        AttackEntityCallback.EVENT.register(EventRegistrar::onAttackEntity);
    }

}