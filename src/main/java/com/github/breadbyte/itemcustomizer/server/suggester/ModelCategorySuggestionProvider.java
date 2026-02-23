package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModelCategorySuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelCategorySuggestionProvider INSTANCE = new ModelCategorySuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        @Nullable
        String paramNamespace;
        @Nullable
        String paramCategory;

        try {
            paramNamespace = String.valueOf(context.getArgument("namespace", String.class));
            paramCategory = String.valueOf(context.getArgument("item_category", String.class));
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }
        if (paramNamespace.isEmpty() || paramCategory.isEmpty())
            return builder.buildFuture();

        // Get all models, and check if we have CUSTOMIZE.<namespace>.<item_name> permission for it.
        var player = context.getSource().getPlayer();
        var instance = ModelsIndex.getInstance();
        var categories = instance.categories(paramNamespace);
        Set<NamespaceCategory> validItemTypes = null;

        if (AccessValidator.IsAdmin(player)) {
            validItemTypes = categories;
        } else {
            validItemTypes = categories.stream()
                    .filter(namespace -> {
                        // We have permission for this category, skip
                        for (NamespaceCategory category : categories) {
                            if (Permissions.check(player, Permission.CUSTOMIZE.chain(category.getPermissionNode()).getPermission())) {
                                return true;
                            }

                            // We have permission for this model, continue
                            for (CustomModelDefinition model : instance.getAllRecursive(category)) {
                                if (model.getPermission(player)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toSet());
        }

        {
            validItemTypes = categories
                    .stream()
                    .filter(n ->
                    Permissions.check(player, Permission.CUSTOMIZE.chain(n.getPermissionNode()).getPermission())
                    ).collect(Collectors.toSet());
        }

        for (NamespaceCategory itemType : validItemTypes) {
            builder.suggest(itemType.getCategory());
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
