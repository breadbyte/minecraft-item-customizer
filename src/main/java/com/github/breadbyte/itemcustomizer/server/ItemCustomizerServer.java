package com.github.breadbyte.itemcustomizer.server;

public class ItemCustomizerServer implements net.fabricmc.api.DedicatedServerModInitializer {

    // Setup Fabric Logger
    public static final String MOD_ID = "itemcustomizer";
    public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {
        LOGGER.info("Item Customizer Server is initializing...");

        // Register server-side components, commands, and event listeners.
        // This is where you would register commands for renaming items, adding lore, etc.
        //ItemCustomizerCommands.registerCommands();
    }
}
