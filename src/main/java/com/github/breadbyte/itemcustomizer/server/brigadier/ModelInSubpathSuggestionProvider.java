package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class ModelInSubpathSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelInSubpathSuggestionProvider INSTANCE = new ModelInSubpathSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String namespace;
        String category;
        try {
            namespace = context.getArgument("namespace", String.class);
            category = context.getArgument("item_category", String.class);
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();

        // Suggest only items in the current exact category
        for (CustomModelDefinition model : index.get(namespace, category)) {
            if (hasPermissionForModel(player, model)) {
                builder.suggest(model.getName());
            }
        }

        return builder.buildFuture();
    }

    private boolean hasPermissionForModel(ServerPlayerEntity player, CustomModelDefinition model) {
        if (AccessValidator.IsAdmin(player)) return true;
        return Permissions.check(player, model.getPermissionNode());
    }
}
