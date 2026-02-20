package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModelNamespaceSuggestionProvider implements SuggestionProvider<ServerCommandSource>  {
    public static final ModelNamespaceSuggestionProvider INSTANCE = new ModelNamespaceSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();
        var allNamespaces = index.namespaces();

        Set<String> validNamespaces;

        if (Check.IsAdmin(player)) {
            validNamespaces = allNamespaces;
        } else {
            validNamespaces = allNamespaces.stream()
                .filter(namespace -> {
                    // We have permission for this namespace, skip
                    if (Permissions.check(player, Check.Permission.CUSTOMIZE.chain(namespace))) {
                        return true;
                    }

                    // We have permission for this category, skip
                    for (String category : index.categories(namespace)) {
                        String namespaceCategoryNode = namespace + "." + category;
                        if (Permissions.check(player, Check.Permission.CUSTOMIZE.chain(namespaceCategoryNode))) {
                            return true;
                        }

                        // We have permission for this model, continue
                        for (CustomModelDefinition model : index.get(namespace, category)) {
                            if (model.getPermission(player)) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .collect(Collectors.toSet());
        }

        for (String namespace : validNamespaces) {
            builder.suggest(namespace);
        }

        return builder.buildFuture();
    }
}
