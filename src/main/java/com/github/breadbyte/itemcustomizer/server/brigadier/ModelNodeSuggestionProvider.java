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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.*;

public class ModelNodeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelNodeSuggestionProvider INSTANCE = new ModelNodeSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String namespace;
        String category;
        try {
            // Normalize namespace and category to lowercase for consistent lookup
            namespace = context.getArgument(NAMESPACE_ARGUMENT, String.class).toLowerCase();
            category = context.getArgument(ITEM_CATEGORY_ARGUMENT, String.class).toLowerCase();
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        List<String> previousNodes = new ArrayList<>();
        // Collect all fully parsed node arguments that precede the current one.
        for (int i = 1; i <= MAX_AUTOCOMPLETE_NODES; i++) {
            String nodeName = NODE_PREFIX + i;
            try {
                // Normalize node values to lowercase for consistent lookup
                String value = context.getArgument(nodeName, String.class).toLowerCase();
                previousNodes.add(value);
            } catch (IllegalArgumentException e) {
                // This means nodeName (or a later node) has not been fully parsed yet.
                // So, all arguments up to nodeName-1 are in previousNodes.
                break;
            }
        }

        // flatten the previous nodes into a path prefix for lookup, but only if we have at least one node parsed
        // if there are no nodes parsed yet, we want to suggest the entire category

        String findItem = "";
        findItem = String.join("/", previousNodes);
        if (findItem.isEmpty()) {
            findItem = category;
        }

        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();

        Set<String> allPotentialSuggestions = new LinkedHashSet<>();

        // 1. Add immediate sub-categories/nodes
        var partialChildren = index.partialChildren(namespace, category, findItem);
        for (String child : partialChildren) {
            if (Objects.equals(child, findItem)) continue; // Skip suggesting the same path as a child of itself

            // TODO
            // We want to suggest the full path of the child, but we need to check permissions on the model it points to
//            String potentialModelPath = findItem.equals(category) ? child : findItem + "/" + child;
//            CustomModelDefinition model = index.get(namespace, potentialModelPath);
//            if (model != null && hasPermissionForModel(player, model)) {
//                allPotentialSuggestions.add(child); // Suggest only the immediate next segment, not the full path
//            }

            allPotentialSuggestions.add(child);
        }

        // 2. Add model names directly under the currentPathPrefix
        if (findItem.equals(category)) {
            // If we're still at the category level, we want to include models directly under the category as well
            for (CustomModelDefinition model : index.get(namespace, category)) {
                if (hasPermissionForModel(player, model)) {
                    allPotentialSuggestions.add(model.getName());
                }
            }
        } else {
            // If we're deeper than the category level, we want to include models that match the partial path
            for (CustomModelDefinition model : index.getPartialPathMatch(namespace, category, findItem)) {
                if (hasPermissionForModel(player, model)) {
                    allPotentialSuggestions.add(model.getName());
                }
            }
        }

        // Get the current input for the argument being typed, for manual filtering
        String remainingInput = builder.getRemaining().toLowerCase();

        // Manually filter and add to the builder
        for (String suggestion : allPotentialSuggestions) {
            if (suggestion.toLowerCase().startsWith(remainingInput)) {
                builder.suggest(suggestion); // Suggest the complete, original-cased name
            }
        }

        return builder.buildFuture();
    }

    private boolean hasPermissionForModel(ServerPlayerEntity player, CustomModelDefinition model) {
        // Player can be null if command is executed by console or a command block.
        if (player == null) return true;
        if (AccessValidator.IsAdmin(player)) return true;
        return Permissions.check(player, model.getPermissionNode());
    }
}