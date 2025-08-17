package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.Storage;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class ItemTypeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ItemTypeSuggestionProvider INSTANCE = new ItemTypeSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        var inst = Cache.getInstance();
        for (String itemType : inst.getItemTypes()) {
            builder.suggest(itemType);
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
