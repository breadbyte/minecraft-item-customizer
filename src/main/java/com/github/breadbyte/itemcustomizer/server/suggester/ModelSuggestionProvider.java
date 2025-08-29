package com.github.breadbyte.itemcustomizer.server.suggester;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

import static com.github.breadbyte.itemcustomizer.server.Check.IsAdmin;

public class ModelSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    public static final ModelSuggestionProvider INSTANCE = new ModelSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        // Can be namespace with the old format, namespace.category with the new format
        var paramItemType = String.valueOf(context.getArgument("item_type", String.class));

        String namespace = null;
        String destination = null;
        ServerPlayerEntity player = context.getSource().getPlayer();

        // If the item type contains a dot, it means it's in the new format (namespace.category).
        // We should split it into namespace and category.
        if (paramItemType.contains(".")) {
            var parts = paramItemType.split("\\.");
            var model = ModelsIndex.INSTANCE.get(parts[0], parts[1]);

            if (!model.isEmpty()) {
                if (!Check.IsAdmin(player)) {
                    // Check if we have permission for the model's namespace
                    if (!Permissions.check(player, Check.Permission.CUSTOMIZE.chain(parts[0])))
                        return builder.buildFuture();

                    // Check if we have permission for the model's category
                    if (!Permissions.check(player, Check.Permission.CUSTOMIZE.chain(paramItemType)))
                        return builder.buildFuture();
                }

                // Check which models we can return as a suggestion

                for (CustomModelDefinition cmd : model) {
                    if (IsAdmin(player) || Check.Permission.CUSTOMIZE.checkPermissionForModel(player, cmd)) {
                        builder.suggest(cmd.getName());
                    }
                }

            } else {
                // Return empty suggestions
                return builder.buildFuture();
            }
        }

        // Lock the suggestions after we've modified them.
        return builder.buildFuture();
    }
}
