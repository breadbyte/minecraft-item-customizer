package com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace.IModelNamespaceOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace.ModelNamespaceParams;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.internal.CSVFetcher;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Postmaster;
import com.github.breadbyte.itemcustomizer.server.util.Result;

public class ModelNamespaceOperations implements IModelNamespaceOperations {

    @Override
    public Result<String> addNamespace(ModelNamespaceParams params) {
        var paramNamespace = params.namespace();
        var paramUrl = params.url();
        var src = params.server();

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
                        if (storeInst.get(defs.getNamespace(), defs.getCategory()) == null) {
                            continue;
                        }
                        storeInst.add(defs);
                    }

                    // Save after processing all suggestions
                    storeInst.save();

                    // Send the suggestions to the player
                    Postmaster.Hud_SendMessage_Yes(params.source(), "Suggestions updated");
                } catch (Exception ignored) {
                    throw ignored;
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
}
