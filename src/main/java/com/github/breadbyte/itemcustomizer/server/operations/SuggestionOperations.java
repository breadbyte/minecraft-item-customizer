package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.Cache;
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
        Helper.SendMessage(context.getSource().getPlayer(), "Suggestions updated", SoundEvents.BLOCK_NOTE_BLOCK_PLING);
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
