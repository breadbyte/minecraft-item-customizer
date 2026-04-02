package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
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
        String paramNamespace;

        try {
            paramNamespace = context.getArgument("namespace", String.class);
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        var player = context.getSource().getPlayer();
        var instance = ModelsIndex.getInstance();
        var allCategories = instance.categories(paramNamespace);
        Set<ModelPath> validCategories;

        if (AccessValidator.IsAdmin(player)) {
            validCategories = allCategories;
        } else {
            validCategories = allCategories.stream()
                    .filter(path -> {
                        // Check if we have permission for this category
                        if (Permissions.check(player, Permission.CUSTOMIZE.chain(path.getPermissionNode()).getPermission())) {
                            return true;
                        }

                        // Check if we have permission for any model in this category
                        return instance.getAllRecursive(path).stream()
                                .anyMatch(model -> Permissions.check(player, model.getPermissionNode()));
                    })
                    .collect(Collectors.toSet());
        }

        for (ModelPath categoryPath : validCategories) {
            builder.suggest(categoryPath.getCategory());
        }

        return builder.buildFuture();
    }
}
