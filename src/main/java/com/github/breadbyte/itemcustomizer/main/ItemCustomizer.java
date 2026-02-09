package com.github.breadbyte.itemcustomizer.main;

import com.github.breadbyte.itemcustomizer.server.CommandRegistration;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemCustomizer implements ModInitializer {

    // Setup Fabric Logger
    public static final String MOD_ID = "itemcustomizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        // This allows the mod to register commands in both the integrated server and dedicated server environments, but not on the client.
        CommandRegistration.RegisterCommands();
    }

}