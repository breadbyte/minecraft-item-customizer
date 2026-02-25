package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
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

public class ModelSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelSuggestionProvider INSTANCE = new ModelSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        @Nullable
        String paramNamespace;
        @Nullable
        String paramCategory;
        @Nullable
        String paramItemType;

        try {
            paramNamespace = String.valueOf(context.getArgument("namespace", String.class));
            paramCategory = String.valueOf(context.getArgument("item_category", String.class));
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        var player = context.getSource().getPlayer();
        var items = ModelsIndex.getInstance().getAllRecursive(new NamespaceCategory(paramNamespace, paramCategory));
        Set<CustomModelDefinition> validItems;

        if (AccessValidator.IsAdmin(player)) {
            validItems = items;
        } else {
            validItems = items
                    .stream()
                    .filter(n ->
                            Permissions.check(player, Permission.CUSTOMIZE.chain(n.getPermissionNode()).getPermission())
                    ).collect(Collectors.toUnmodifiableSet());
        }

        for (CustomModelDefinition item : validItems) {
            builder.suggest(item.getName());
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
