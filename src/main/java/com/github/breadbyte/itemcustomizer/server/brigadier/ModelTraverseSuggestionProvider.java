package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModelTraverseSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelTraverseSuggestionProvider INSTANCE = new ModelTraverseSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String namespace;
        try {
            namespace = context.getArgument("namespace", String.class);
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }

        String remaining = builder.getRemaining();
        int lastSlash = remaining.lastIndexOf('/');
        String currentPath = lastSlash == -1 ? "" : remaining.substring(0, lastSlash);
        String prefix = lastSlash == -1 ? remaining : remaining.substring(lastSlash + 1);

        // Offset the builder to the start of the current segment (after the last slash)
        SuggestionsBuilder subBuilder = builder.createOffset(builder.getStart() + lastSlash + 1);

        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();

        // 1. Suggest immediate sub-categories
        for (String child : index.immediateChildren(namespace, currentPath)) {
            if (child.startsWith(prefix)) {
                String fullPathToChild = currentPath.isEmpty() ? child : currentPath + "/" + child;

                if (hasPermissionForPath(player, index, ModelPath.fromNamespaceAndPath(namespace, fullPathToChild))) {
                    // Suggest the directory itself
                    subBuilder.suggest(child + "/");

                    // Only perform look-ahead for vanilla clients.
                    // Modded clients use the ChatInputSuggestorMixin to force a recalculation.
                    for (String grandchild : index.immediateChildren(namespace, fullPathToChild)) {
                        subBuilder.suggest(child + "/" + grandchild + "/");
                    }
                    for (CustomModelDefinition nestedModel : index.get(namespace, fullPathToChild)) {
                        if (hasPermissionForModel(player, nestedModel)) {
                            subBuilder.suggest(child + "/" + nestedModel.getName());
                        }
                    }
                }
            }
        }

        // Suggest items in the current category
        for (CustomModelDefinition model : index.get(namespace, currentPath)) {
            if (model.getName().startsWith(prefix)) {
                if (hasPermissionForModel(player, model)) {
                    subBuilder.suggest(model.getName()); // Only suggest the name, offset handles the path
                }
            }
        }

        return subBuilder.buildFuture();
    }

    private boolean hasPermissionForPath(ServerPlayerEntity player, ModelsIndex index, ModelPath path) {
        if (AccessValidator.IsAdmin(player)) return true;
        
        // Direct permission for category (all segments of the path must be allowed)
        if (Permissions.check(player, Permission.CUSTOMIZE.chain(path.getPermissionNode()).getPermission())) {
            return true;
        }

        // Permission for any model within this path (recursively)
        for (CustomModelDefinition model : index.getAllRecursive(path)) {
            if (hasPermissionForModel(player, model)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermissionForModel(ServerPlayerEntity player, CustomModelDefinition model) {
        if (AccessValidator.IsAdmin(player)) return true;
        return Permissions.check(player, model.getPermissionNode());
    }
}
