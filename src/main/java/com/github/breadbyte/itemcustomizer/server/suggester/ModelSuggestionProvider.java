package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

import static com.github.breadbyte.itemcustomizer.server.Check.IsAdmin;

public class ModelSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelSuggestionProvider INSTANCE = new ModelSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var paramItemType = String.valueOf(context.getArgument("item_type", String.class));

        // Shouldn't be null at this point but eh
        if (paramItemType == null)
            return builder.buildFuture();

        // Suggest only the item names that match the given item type.
        // Make sure that we have CUSTOMIZE.<namespace>.<item_name> permission for it.
        var inst = Cache.getInstance();
        var player = context.getSource().getPlayer();
        java.util.function.Predicate<CustomModelDefinition> valid =
                model -> model.getItemType().equals(paramItemType.split("\\.")[1]) &&
                        (IsAdmin(player) || Check.Permission.CUSTOMIZE.checkPermissionForNamespace(player, model.getPermissionNode()));

        var s = inst.getCustomModels()
                .stream()
                .filter(valid)
                .map(CustomModelDefinition::getItemName)
                .distinct().toList();

        for (String itemName : s) {
            builder.suggest(itemName);
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }

}
