package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import net.fabricmc.api.DedicatedServerModInitializer;

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
