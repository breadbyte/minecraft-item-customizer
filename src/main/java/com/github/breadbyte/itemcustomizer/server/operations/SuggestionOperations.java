package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.Storage;
import com.github.breadbyte.itemcustomizer.server.suggester.builder.CSVFetcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;

public class SuggestionOperations {
    public static int registerSuggestions(CommandContext<ServerCommandSource> context) {
        var paramUrl = String.valueOf(context.getArgument("csv_url", String.class));
        var paramNamespace = String.valueOf(context.getArgument("namespace", String.class));

        // Fetch our data
        var suggests = CSVFetcher.fetch(paramNamespace, paramUrl);

        // Load an instance of the storage handler
        Helper.tryLoadStorage();
        var storeInst = Storage.HANDLER.instance();

        // Convert the model data to a list of suggestions
        for (CustomModelDefinition defs : suggests) {
            if (storeInst.CustomModels.contains(defs)) {
                continue;
            }
            storeInst.CustomModels.add(defs);
        }

        // Save after processing all suggestions
        Storage.HANDLER.save();


        // Send the suggestions to the player
        Helper.SendMessage(context.getSource().getPlayer(), "Suggestions updated", SoundEvents.BLOCK_NOTE_BLOCK_PLING);
        return 1;
    }

    public static int clearSuggestions(CommandContext<ServerCommandSource> context) {
        Helper.tryLoadStorage();

        var inst = Storage.HANDLER.instance();
        inst.CustomModels.clear();
        Storage.HANDLER.save();

        Helper.SendMessage(context.getSource().getPlayer(), "All custom model data suggestions cleared!", SoundEvents.BLOCK_NOTE_BLOCK_BASS);
        return 1;
    }

    public static int removeNamespace(CommandContext<ServerCommandSource> context) {
        var paramNamespace = String.valueOf(context.getArgument("namespace", String.class));
        Helper.tryLoadStorage();

        var inst = Storage.HANDLER.instance();

        if (inst.CustomModels.removeIf(def -> def.getNamespace().equals(paramNamespace))) {
            Helper.SendMessage(context.getSource().getPlayer(), "Namespace " + paramNamespace + " removed successfully!", SoundEvents.BLOCK_NOTE_BLOCK_PLING);
            Storage.HANDLER.save();
        } else {
            Helper.SendMessage(context.getSource().getPlayer(), "Namespace " + paramNamespace + " not found!", SoundEvents.BLOCK_NOTE_BLOCK_BASS);
            return 0;
        }
        return 1;
    }

}
