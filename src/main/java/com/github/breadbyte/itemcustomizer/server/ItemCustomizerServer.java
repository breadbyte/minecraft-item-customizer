package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.server.data.Cache;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ItemCustomizerServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        // Load existing definitions, if available
        Helper.tryLoadStorage();

        // Initialize the cache
        Cache inst = Cache.getInstance();
        inst.initialize();
    }
}
