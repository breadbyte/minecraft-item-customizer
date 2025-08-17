package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class ModelSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelSuggestionProvider INSTANCE = new ModelSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var paramItemType = String.valueOf(context.getArgument("item_type", String.class));

        // Shouldn't be null at this point but eh
        if (paramItemType == null)
            return builder.buildFuture();

        // Suggest only the item names that match the given item type.
        var inst = Cache.getInstance();
        inst.getCustomModelsCache()
                .stream()
                .filter(model -> model.getItemType().equals(paramItemType))
                .map(CustomModelDefinition::getItemName).distinct().forEach(builder::suggest);

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }

}
