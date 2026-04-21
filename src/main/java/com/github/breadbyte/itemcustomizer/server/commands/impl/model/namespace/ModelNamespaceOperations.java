package com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace.IModelNamespaceOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace.ModelNamespaceParams;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.internal.CSVFetcher;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Postmaster;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;

import java.net.URL;

public class ModelNamespaceOperations implements IModelNamespaceOperations {

    @Override
    public Result<String> addNamespace(ModelNamespaceParams params) {
        var paramNamespace = params.namespace();
        URL paramUrl;
        try {
            paramUrl = params.url();
        } catch (Exception e) {
            return Result.err(new Reason.InternalError("Invalid URL: " + e.getMessage() + " " + params.url()));
        }
        var src = params.server();

        // Store the URL
        ModelsIndex.getInstance().setNamespaceUrl(paramNamespace, paramUrl.toString());

        // Start async fetch to avoid blocking the server thread.
        // The message sending mechanism inside /must/ use Postmaster to ensure thread safety.
        CSVFetcher.fetchAsync(paramNamespace, paramUrl).whenComplete((suggests, throwable) -> {
            if (throwable != null) {
                // Post back to the server thread to interact with player safely.
                src.execute(() -> {
                    try {
                        Postmaster.Hud_SendMessage_No(params.source(), "Failed to fetch suggestions: " + throwable.getMessage());
                    } catch (Exception ignored) {}
                });
                return;
            }

            // Apply results on the server thread.
            src.execute(() -> {
                try {
                    // Load an instance of the storage cache
                    var storeInst = ModelsIndex.getInstance();
                    storeInst.load();

                    // Convert the model data to a list of suggestions
                    for (CustomModelDefinition defs : suggests) {
                        storeInst.add(defs);
                    }

                    // Save after processing all suggestions
                    storeInst.save();

                    // Send the suggestions to the player
                    Postmaster.Hud_SendMessage_Yes(params.source(), "Suggestions updated for " + paramNamespace);
                } catch (Exception ex) {
                    Postmaster.Hud_SendMessage_No(params.source(), "Failed to fetch suggestions: " + ex.getMessage());
                }
            });
        });


        return Result.ok("Fetching suggestions...");
    }

    @Override
    public Result<String> removeNamespace(ModelNamespaceParams params) {
        var paramNamespace = params.namespace();
        Helper.tryLoadStorage();

        var inst = ModelsIndex.getInstance();
        return inst.removeNamespace(paramNamespace);
    }

    @Override
    public Result<String> clearAll(ModelNamespaceParams params) {
        Helper.tryLoadStorage();

        var inst = ModelsIndex.getInstance();
        inst.clear();
        inst.save();

        return Result.ok();
    }

    public Result<String> refreshNamespace(ModelNamespaceParams params) {
        var paramNamespace = params.namespace();
        var urlStr = ModelsIndex.getInstance().getNamespaceUrl(paramNamespace);
        if (urlStr == null) {
            return Result.err(new Reason.InternalError("No URL stored for namespace: " + paramNamespace));
        }

        try {
            URL url = new URL(urlStr);
            // Re-using addNamespace logic via a helper or direct call if params can be constructed
            // But we can just call addNamespace with new params if we had the URL.
            // Since we're in Operations, we can just trigger the fetch.
            
            var src = params.server();
            CSVFetcher.fetchAsync(paramNamespace, url).whenComplete((suggests, throwable) -> {
                if (throwable != null) {
                    src.execute(() -> {
                        try {
                            Postmaster.Hud_SendMessage_No(params.source(), "Failed to refresh " + paramNamespace + ": " + throwable.getMessage());
                        } catch (Exception ignored) {}
                    });
                    return;
                }

                src.execute(() -> {
                    try {
                        var storeInst = ModelsIndex.getInstance();
                        // Optional: clear existing models for this namespace before refreshing?
                        // The user said "refresh and reload", which usually means replace.
                        storeInst.removeNamespace(paramNamespace);
                        storeInst.setNamespaceUrl(paramNamespace, urlStr); // Restore URL since removeNamespace clears it

                        for (CustomModelDefinition defs : suggests) {
                            storeInst.add(defs);
                        }
                        storeInst.save();
                        Postmaster.Hud_SendMessage_Yes(params.source(), "Namespace " + paramNamespace + " refreshed.");
                    } catch (Exception ex) {
                        Postmaster.Hud_SendMessage_No(params.source(), "Failed to refresh " + paramNamespace + ": " + ex.getMessage());
                    }
                });
            });
            return Result.ok("Refreshing " + paramNamespace + "...");
        } catch (Exception e) {
            return Result.err(new Reason.InternalError("Stored URL is invalid: " + urlStr));
        }
    }
}
