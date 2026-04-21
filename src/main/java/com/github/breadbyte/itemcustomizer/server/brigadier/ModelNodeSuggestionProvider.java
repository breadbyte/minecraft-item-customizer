package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
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
import static com.github.breadbyte.itemcustomizer.server.data.ModelsIndex.depthOf;
import static com.github.breadbyte.itemcustomizer.server.util.Helper.trimTrailingSlash;

public class ModelNodeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelNodeSuggestionProvider INSTANCE = new ModelNodeSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String namespace;
        try {
            // Normalize namespace and category to lowercase for consistent lookup
            namespace = context.getArgument(NAMESPACE_ARGUMENT, String.class).toLowerCase();
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        List<String> previousNodes = new ArrayList<>();
        int NODES_PARSED_INDEXED_BY_ONE = 0;

        // Collect all fully parsed node arguments that precede the current one.
        for (int i = 1; i <= MAX_AUTOCOMPLETE_NODES; i++) {
            String nodeName = NODE_PREFIX + i;
            try {
                // Normalize node values to lowercase for consistent lookup
                String value = context.getArgument(nodeName, String.class).toLowerCase();
                previousNodes.add(value);
                NODES_PARSED_INDEXED_BY_ONE++;
            } catch (IllegalArgumentException e) {
                // This means nodeName (or a later node) has not been fully parsed yet.
                // So, all arguments up to nodeName-1 are in previousNodes.
                break;
            }
        }

        if (NODES_PARSED_INDEXED_BY_ONE == 1) {
            var firstNode = previousNodes.getFirst();
            if (firstNode.contains("/")) {
                // TODO: Parse this as a direct string, and don't use autocomplete
            }
        }

        // flatten the previous nodes into a path prefix for lookup, but only if we have at least one node parsed
        // if there are no nodes parsed yet, we want to suggest the entire category

        String findItem = "";
        findItem = namespace + ":" + String.join("/", previousNodes);
        findItem = trimTrailingSlash(findItem);


        // Manually filter and add to the builder
        for (String suggestion : ModelsIndex.INSTANCE.__internalAutocomplete(findItem)) {
            builder.suggest(suggestion);
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