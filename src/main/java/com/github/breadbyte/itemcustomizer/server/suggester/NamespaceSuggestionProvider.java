package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.Storage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class NamespaceSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final NamespaceSuggestionProvider INSTANCE = new NamespaceSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        var inst = Cache.getInstance();
        for (String namespace : inst.getNamespaces()) {
            builder.suggest(namespace);
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
