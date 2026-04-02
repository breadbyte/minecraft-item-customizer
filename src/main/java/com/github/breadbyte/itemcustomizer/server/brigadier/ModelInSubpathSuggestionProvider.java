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

        String remaining = builder.getRemaining();
        int lastSlash = remaining.lastIndexOf('/');
        String currentSubPath = lastSlash == -1 ? "" : remaining.substring(0, lastSlash);
        String prefix = lastSlash == -1 ? remaining : remaining.substring(lastSlash + 1);

        // Offset the builder to the start of the current segment (after the last slash)
        SuggestionsBuilder subBuilder = builder.createOffset(builder.getStart() + lastSlash + 1);

        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();

        // category and subPath are both parts of the "category" in ModelsIndex.
        String fullCategoryPath = category;
        if (!currentSubPath.isEmpty()) {
            fullCategoryPath = category + "/" + currentSubPath;
        }

        // 1. Suggest immediate sub-categories
        for (String child : index.immediateChildren(namespace, fullCategoryPath)) {
            if (child.startsWith(prefix)) {
                String fullPathToChild = fullCategoryPath.isEmpty() ? child : fullCategoryPath + "/" + child;

                if (hasPermissionForPath(player, index, ModelPath.fromNamespaceAndPath(namespace, fullPathToChild))) {
                    subBuilder.suggest(child + "/");
                }
            }
        }

        // Suggest items in the current category
        for (CustomModelDefinition model : index.get(namespace, fullCategoryPath)) {
            if (model.getName().startsWith(prefix)) {
                if (hasPermissionForModel(player, model)) {
                    subBuilder.suggest(model.getName());
                }
            }
        }

        return subBuilder.buildFuture();
    }

    private boolean hasPermissionForPath(ServerPlayerEntity player, ModelsIndex index, ModelPath path) {
        if (AccessValidator.IsAdmin(player)) return true;
        
        if (Permissions.check(player, Permission.CUSTOMIZE.chain(path.getPermissionNode()).getPermission())) {
            return true;
        }

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
