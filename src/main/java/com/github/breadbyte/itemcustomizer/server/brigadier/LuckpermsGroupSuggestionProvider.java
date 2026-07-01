package com.github.breadbyte.itemcustomizer.server.brigadier;

import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelGroupCommand;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.NAMESPACE_ARGUMENT;

public class LuckpermsGroupSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String paramGroup;

        try {
            paramGroup = context.getArgument(ModelGroupCommand.GROUP_NAME_ARGUMENT, String.class);
            var groups = LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(context.getSource().getPlayer()).getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build());
            List<String> groupReadable = new ArrayList<String>();
            for (var group : groups) {
                 groupReadable.add(group.getDisplayName());
            }
            return CommandSource.suggestMatching(groupReadable, builder);
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }
    }
}
