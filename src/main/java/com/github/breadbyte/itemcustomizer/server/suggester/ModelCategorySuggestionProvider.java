package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModelCategorySuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelCategorySuggestionProvider INSTANCE = new ModelCategorySuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        // Get all models, and check if we have CUSTOMIZE.<namespace>.<item_name> permission for it.
        var player = context.getSource().getPlayer();
        var categories = ModelsIndex.getInstance().namespaceCategories();
        Set<NamespaceCategory> validItemTypes = null;

        if (Check.IsAdmin(player)) {
            validItemTypes = categories;
        } else {
            validItemTypes = categories
                    .stream()
                    .filter(n ->
                    Permissions.check(player, Check.Permission.CUSTOMIZE.chain(n.getPermissionNode()))
                    ).collect(Collectors.toSet());
        }

        for (NamespaceCategory itemType : validItemTypes) {
            builder.suggest(itemType.getCategory());
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
