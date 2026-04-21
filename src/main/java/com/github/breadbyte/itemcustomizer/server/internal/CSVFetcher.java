package com.github.breadbyte.itemcustomizer.server.internal;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class CSVFetcher {
    public static CompletableFuture<ArrayList<CustomModelDefinition>> fetchAsync(@NotNull String namespace, URL csvUrl) {
        // Perform network I/O off the server thread and return the parsed models.
        return CompletableFuture.supplyAsync(() -> parseCsv(namespace, csvUrl));
    }

    private static ArrayList<CustomModelDefinition> parseCsv(@NotNull String namespace, URL csvUrl) {
        ArrayList<CustomModelDefinition> suggestions = new ArrayList<>();

        // CSV 0 = item type
        // CSV 1 = item category
        // CSV 2 = item name
        // CSV 3 = made by
        // CSV 4 = destination path

        try {
            URLConnection connection = csvUrl.openConnection();
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
                        String itemCategory = parts[1].trim();
                        String itemName = parts[2].trim();
                        String madeBy = parts[3].trim();

                        // Destination is the "real" path where the model exists.
                        // It always starts with itemCategory and ends with itemName.
                        // It may or may not have hidden subpaths in between.
                        String destination = parts.length > 4 ? parts[4].replace("\"", "").trim() : "";

                        if (itemType.isEmpty() || itemName.isEmpty()) {
                            ItemCustomizer.LOGGER.warn("Skipping invalid CSV line (empty type or name): {}", line);
                            continue;
                        }

                        ModelPath nc = ModelPath.of(namespace + ":" + destination);
                        suggestions.add(new CustomModelDefinition(nc, madeBy));
                    } else {
                        //ItemCustomizer.LOGGER.warn("CSV line does not contain enough parts: {}", line);
                    }
                }
            }
        } catch (IOException e) {
            ItemCustomizer.LOGGER.error("Error fetching CSV data from URL: {}", csvUrl, e);
        }

        return suggestions;
    }
}
