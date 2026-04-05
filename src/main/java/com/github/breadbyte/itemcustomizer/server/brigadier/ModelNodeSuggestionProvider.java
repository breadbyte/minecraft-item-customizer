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
        // In Brigadier, the SuggestionProvider is called for the current argument.
        // We can determine which node we are currently at by counting how many
        // node arguments are already present in the context and finished.
        for (int i = 1; i <= MAX_AUTOCOMPLETE_NODES; i++) {
            String nodeName = NODE_PREFIX + i;
            try {
                String value = context.getArgument(nodeName, String.class);
                
                // If the user is typing nodeX, nodeX might already be in the context
                // if it's been partially parsed. However, we want 'previousNodes' to
                // only contain the fully completed path BEFORE the current node.
                
                // We can check if this argument's value is what's being suggested for
                // by seeing if it's the last thing in the input and matches what's 
                // currently being typed.
                
                int cursor = builder.getStart();
                String input = context.getInput();
                // Find if the argument value starts at or after the cursor.
                // If it does, then it's the argument we are currently typing.
                
                // Simplified approach: Brigadier calls the provider for the 'active' node.
                // All nodes in the path before it should be fully present.
                // If node i is present, let's see if its end index in the input 
                // is before or at the start of the current suggestions builder.
                
                // Since we don't have getArguments().get(nodeName).getRange(), we can
                // use lastIndexOf to guess where it is, or just assume the one that
                // triggers IllegalArgumentException or matches builder.getRemaining()
                // is the current one.
                
                if (input.substring(0, cursor).trim().endsWith(value)) {
                    previousNodes.add(value);
                } else {
                    break;
                }
            } catch (IllegalArgumentException e) {
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
