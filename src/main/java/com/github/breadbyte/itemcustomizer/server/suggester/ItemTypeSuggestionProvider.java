package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.Storage;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

import static com.github.breadbyte.itemcustomizer.server.Check.IsAdmin;

public class ItemTypeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ItemTypeSuggestionProvider INSTANCE = new ItemTypeSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        // Get all models, and check if we have CUSTOMIZE.<namespace>.<item_name> permission for it.
        var inst = Cache.getInstance();
        var player = context.getSource().getPlayer();
        var validItemTypes = inst.getNamespace_ItemType_s()
                .stream()
                .filter(
                        n ->
                                Check.Permission.CUSTOMIZE.checkPermissionForStringSelector(player, n) ||
                                Check.IsAdmin(player))
                .toList();


        for (String itemType : validItemTypes) {
            builder.suggest(itemType);
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
