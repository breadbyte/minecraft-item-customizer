package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Storage {

    // Deprecated to catch external access, only for internal use
    // Use Cache instead
    @Deprecated
    public static ConfigClassHandler<Storage> HANDLER = ConfigClassHandler.createBuilder(Storage.class)
            .id(Identifier.of(ItemCustomizer.MOD_ID, "item_customizer"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("cache.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public List<CustomModelDefinition> CustomModels = new ArrayList<>();
}
