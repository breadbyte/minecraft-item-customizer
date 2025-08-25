package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.suggester.builder.CSVFetcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;

public class SuggestionOperations {
    public static int registerSuggestions(CommandContext<ServerCommandSource> context) {
        var paramUrl = String.valueOf(context.getArgument("csv_url", String.class));
        var paramNamespace = String.valueOf(context.getArgument("namespace", String.class));

        var source = context.getSource();

        // Start async fetch to avoid blocking the server thread.
        CSVFetcher.fetchAsync(paramNamespace, paramUrl).whenComplete((suggests, throwable) -> {
            if (throwable != null) {
                // Post back to the server thread to interact with player safely.
                source.getServer().execute(() -> {
                    try {
                        Helper.SendMessageNo(source.getPlayer(), "Failed to fetch suggestions: " + throwable.getMessage());
                    } catch (Exception ignored) {}
                });
                return;
            }

            // Apply results on the server thread.
            source.getServer().execute(() -> {
                try {
                    // Load an instance of the storage cache
                    var storeInst = Cache.getInstance();
                    storeInst.load();

                    // Convert the model data to a list of suggestions
                    for (CustomModelDefinition defs : suggests) {
                        if (storeInst.getCustomModels().contains(defs)) {
                            continue;
                        }
                        storeInst.add(defs);
                    }

                    // Save after processing all suggestions
                    storeInst.save();

                    // Send the suggestions to the player
                    Helper.SendMessage(source.getPlayer(), "Suggestions updated", SoundEvents.ENTITY_PLAYER_LEVELUP);
                } catch (Exception ignored) {
                    // Player may have disconnected or context invalid
                }
            });
        });

        // Immediate feedback and return without blocking.
        try {
            Helper.SendMessage(source.getPlayer(), "Fetching suggestions...", SoundEvents.BLOCK_NOTE_BLOCK_BASS);
        } catch (Exception ignored) {}
        return 1;
    }

    public static int clearSuggestions(CommandContext<ServerCommandSource> context) {
        Helper.tryLoadStorage();

        var inst = Cache.getInstance();
        inst.clear();
        inst.save();

        Helper.SendMessage(context.getSource().getPlayer(), "All custom model data suggestions cleared!", SoundEvents.BLOCK_NOTE_BLOCK_BASS);
        return 1;
    }

    public static int removeNamespace(CommandContext<ServerCommandSource> context) {
        var paramNamespace = String.valueOf(context.getArgument("namespace", String.class));
        Helper.tryLoadStorage();

        var inst = Cache.getInstance();
        var result = inst.removeNamespace(paramNamespace);

        if (result.ok())
            Helper.SendMessage(context.getSource().getPlayer(), result.details(), SoundEvents.BLOCK_NOTE_BLOCK_PLING);
        else
            Helper.SendMessageNo(context.getSource().getPlayer(), result.details());

        return 1;
    }

}
