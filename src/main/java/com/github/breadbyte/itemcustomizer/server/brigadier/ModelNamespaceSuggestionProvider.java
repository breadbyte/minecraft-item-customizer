package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModelNamespaceSuggestionProvider implements SuggestionProvider<ServerCommandSource>  {
    public static final ModelNamespaceSuggestionProvider INSTANCE = new ModelNamespaceSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var player = context.getSource().getPlayer();
        var index = ModelsIndex.getInstance();
        var allNamespaces = index.getNamespaces();

        List<String> validNamespaces;

        if (AccessValidator.IsAdmin(player)) {
            validNamespaces = allNamespaces;
        } else {
            validNamespaces = allNamespaces.stream()
                .filter(namespace -> {
                    return Permissions.check(player, Permission.CUSTOMIZE.chain(namespace).getPermission());
                })
                .toList();
        }

        return CommandSource.suggestMatching(validNamespaces, builder);
    }
}
