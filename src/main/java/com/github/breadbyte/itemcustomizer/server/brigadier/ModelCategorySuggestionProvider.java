package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.NAMESPACE_ARGUMENT;

public class ModelCategorySuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelCategorySuggestionProvider INSTANCE = new ModelCategorySuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String paramNamespace;

        try {
            paramNamespace = context.getArgument(NAMESPACE_ARGUMENT, String.class);
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        var instance = ModelsIndex.getInstance();
        
        // Suggest only the top-level segments as "categories"
        return CommandSource.suggestMatching(instance.immediateChildren(paramNamespace, ""), builder);
    }
}
