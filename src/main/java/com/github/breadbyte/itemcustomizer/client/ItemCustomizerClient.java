package com.github.breadbyte.itemcustomizer.client;

public class ItemCustomizerClient implements net.fabricmc.api.ClientModInitializer {

    // Setup Fabric Logger
    public static final String MOD_ID = "itemcustomizer";
    public static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Item Customizer Client is initializing...");

        // Register client-side components, if any.
        // This is where you would register custom item renderers, etc.
    }
}
