package com.github.breadbyte.itemcustomizer.server.operations.model;

import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.internal.CSVFetcher;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Postmaster;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class NamespaceOperations {
    public static Result<String> registerSuggestions(PlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var paramUrl = String.valueOf(ctx.getArgument("url", String.class));
        var paramNamespace = String.valueOf(ctx.getArgument("namespace", String.class));

        var src = ctx.getSource();

        // Start async fetch to avoid blocking the server thread.
        // The message sending mechanism inside /must/ use Postmaster to ensure thread safety.
        CSVFetcher.fetchAsync(paramNamespace, paramUrl).whenComplete((suggests, throwable) -> {
            if (throwable != null) {
                // Post back to the server thread to interact with player safely.
                src.getServer().execute(() -> {
                    try {
                        Postmaster.Hud_SendMessage_No(src, "Failed to fetch suggestions: " + throwable.getMessage());
                    } catch (Exception ignored) {}
                });
                return;
            }

            // Apply results on the server thread.
            src.getServer().execute(() -> {
                try {
                    // Load an instance of the storage cache
                    var storeInst = ModelsIndex.getInstance();
                    storeInst.load();

                    // Convert the model data to a list of suggestions
                    for (CustomModelDefinition defs : suggests) {
                        if (storeInst.get(defs.getNamespace(), defs.getCategory()) == null) {
                            continue;
                        }
                        storeInst.add(defs);
                    }

                    // Save after processing all suggestions
                    storeInst.save();

                    // Send the suggestions to the player
                    Postmaster.Hud_SendMessage_Yes(src, "Suggestions updated");
                } catch (Exception ignored) {
                    throw ignored;
                }
            });
        });


        return Result.ok("Fetching suggestions...");
    }

    public static Result<Void> clearSuggestions(PlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        Helper.tryLoadStorage();

        var inst = ModelsIndex.getInstance();
        inst.clear();
        inst.save();

        return Result.ok();
    }

    public static Result<String> removeNamespace(PlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var paramNamespace = String.valueOf(ctx.getArgument("namespace", String.class));
        Helper.tryLoadStorage();

        var inst = ModelsIndex.getInstance();
        return inst.removeNamespace(paramNamespace);
    }

}
