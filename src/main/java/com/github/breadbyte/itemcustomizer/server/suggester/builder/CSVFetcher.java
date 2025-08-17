package com.github.breadbyte.itemcustomizer.server.suggester.builder;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.Storage;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class CSVFetcher {
    public static ArrayList<CustomModelDefinition> fetch(@NotNull String namespace, @NotNull String csvUrl) {
        ArrayList<CustomModelDefinition> suggestions = new ArrayList<>();

        // CSV 0 = item type
        // CSV 1 = item name
        // CSV 2 = item path (ignored)
        // CSV 3 = destination

        try {
            URL url = new URL(csvUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                if (lineNumber++ == 0) {
                    // Skip the header line
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String itemType = parts[0].trim();
                    String itemName = parts[1].trim();
                    String destination = parts[3].replace('"', ' ').trim(); // Replace backslashes with forward slashes

                    if (itemType.isEmpty() || itemName.isEmpty() || destination.isEmpty()) {
                        ItemCustomizer.LOGGER.warn("Skipping invalid CSV line: {}", line);
                        continue; // Skip invalid lines
                    }

                    // Create a tuple and add it to the suggestions list
                    suggestions.add(new CustomModelDefinition(namespace, itemType, itemName, destination));
                } else {
                    throw new IllegalArgumentException("CSV line does not contain enough parts: " + line);
                }
            }
            reader.close();
        } catch (IOException e) {
            ItemCustomizer.LOGGER.error("Error fetching CSV data from URL: {}", csvUrl, e);
        }

        // Store all the suggestions in the storage handler
        Storage.HANDLER.load();
        var storeInst = Storage.HANDLER.instance();
        storeInst.CustomModels.addAll(suggestions);
        Storage.HANDLER.save();

        // Also store in the cache
        var cacheInst = Cache.getInstance();
        cacheInst.addAll(suggestions);

        return suggestions;
    }
}
