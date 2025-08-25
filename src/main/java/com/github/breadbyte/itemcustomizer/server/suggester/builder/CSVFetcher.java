package com.github.breadbyte.itemcustomizer.server.suggester.builder;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class CSVFetcher {
    public static CompletableFuture<ArrayList<CustomModelDefinition>> fetchAsync(@NotNull String namespace, @NotNull String csvUrl) {
        // Perform network I/O off the server thread and return the parsed models.
        return CompletableFuture.supplyAsync(() -> parseCsv(namespace, csvUrl));
    }

    private static ArrayList<CustomModelDefinition> parseCsv(@NotNull String namespace, @NotNull String csvUrl) {
        ArrayList<CustomModelDefinition> suggestions = new ArrayList<>();

        // CSV 0 = item type
        // CSV 1 = item name
        // CSV 2 = item path (ignored)
        // CSV 3 = destination

        try {
            URL url = new URL(csvUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(15_000);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    if (lineNumber++ == 0) {
                        // Skip the header line
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String itemType = parts[0].trim();
                        String itemName = parts[1].trim();
                        String destination = parts[3].replace('"', ' ').trim();

                        if (itemType.isEmpty() || itemName.isEmpty() || destination.isEmpty()) {
                            ItemCustomizer.LOGGER.warn("Skipping invalid CSV line: {}", line);
                            continue; // Skip invalid lines
                        }

                        // Create a tuple and add it to the suggestions list
                        suggestions.add(new CustomModelDefinition(namespace, itemType, itemName, destination));
                    } else {
                        ItemCustomizer.LOGGER.warn("CSV line does not contain enough parts: {}", line);
                    }
                }
            }
        } catch (IOException e) {
            ItemCustomizer.LOGGER.error("Error fetching CSV data from URL: {}", csvUrl, e);
        }

        return suggestions;
    }
}
