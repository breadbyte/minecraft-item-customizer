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
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.*;

public class ModelNodeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelNodeSuggestionProvider INSTANCE = new ModelNodeSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String namespace;
        String category;
        try {
            namespace = context.getArgument(NAMESPACE_ARGUMENT, String.class);
            category = context.getArgument(ITEM_CATEGORY_ARGUMENT, String.class);
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        List<String> previousNodes = new ArrayList<>();
        // Collect all fully parsed node arguments that precede the current one.
        for (int i = 1; i <= MAX_AUTOCOMPLETE_NODES; i++) {
            String nodeName = NODE_PREFIX + i;
            try {
                String value = context.getArgument(nodeName, String.class);
                previousNodes.add(value);
            } catch (IllegalArgumentException e) {
                // This means nodeName (or a later node) has not been fully parsed yet.
                // So, all arguments up to nodeName-1 are in previousNodes.
                break;
            }
        }

        String currentPath = category;
        if (!previousNodes.isEmpty()) {
            currentPath += "/" + String.join("/", previousNodes);
        }

        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();

        List<String> suggestions = new ArrayList<>();

        // Add sub-categories
        suggestions.addAll(index.immediateChildren(namespace, currentPath));

        // Add items
        for (CustomModelDefinition model : index.get(namespace, currentPath)) {
            if (hasPermissionForModel(player, model)) {
                suggestions.add(model.getName());
            }
        }

        return CommandSource.suggestMatching(suggestions, builder);
    }

    private boolean hasPermissionForModel(ServerPlayerEntity player, CustomModelDefinition model) {
        if (AccessValidator.IsAdmin(player)) return true;
        return Permissions.check(player, model.getPermissionNode());
    }
}
