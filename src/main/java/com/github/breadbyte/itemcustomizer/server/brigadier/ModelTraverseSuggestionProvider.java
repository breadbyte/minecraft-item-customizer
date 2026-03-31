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
import net.minecraft.server.network.ServerPlayerEntity;
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

        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();

        // Suggest immediate sub-categories
        for (String child : index.immediateChildren(namespace, currentPath)) {
            if (child.startsWith(prefix)) {
                String suggestion = currentPath.isEmpty() ? child : currentPath + "/" + child;
                if (hasPermissionForPath(player, index, new ModelPath(namespace, suggestion))) {
                    builder.suggest(suggestion + "/");
                }
            }
        }

        // Suggest items in the current category
        for (CustomModelDefinition model : index.get(namespace, currentPath)) {
            if (model.getName().startsWith(prefix)) {
                String suggestion = currentPath.isEmpty() ? model.getName() : currentPath + "/" + model.getName();
                if (hasPermissionForModel(player, model)) {
                    builder.suggest(suggestion);
                }
            }
        }

        return builder.buildFuture();
    }

    private boolean hasPermissionForPath(ServerPlayerEntity player, ModelsIndex index, ModelPath path) {
        if (AccessValidator.IsAdmin(player)) return true;
        
        // Direct permission for category
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
