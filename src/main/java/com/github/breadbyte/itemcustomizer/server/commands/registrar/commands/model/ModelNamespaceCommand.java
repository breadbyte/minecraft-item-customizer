package com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model;

import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.operations.SuggestionOperations;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelNamespaceCommand implements BaseCommand {

    public static final String NAMESPACE_ARGUMENT = "namespace";
    public static final String URL_ARGUMENT = "url";

    @Override
    public void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var subCommand = InternalHelper.RequirePermissionFor(literal(subCommandName), permission);

        var RegisterNode = literal("register");
        var NamespaceNode = CommandManager.argument(NAMESPACE_ARGUMENT, StringArgumentType.word());
        var UrlNode = CommandManager.argument(URL_ARGUMENT, StringArgumentType.greedyString());

        var ClearNode = literal("clear");

        var RemoveNode = literal("remove");
        // namespace node here

        // namespace register namespace url
        dispatcher.register(root
                .then(subCommand
                .then(RegisterNode
                .then(NamespaceNode
                .then(UrlNode
                .executes(SuggestionOperations::registerSuggestions))))));

        // namespace clear
        dispatcher.register(root
                .then(subCommand
                .then(ClearNode
                .executes(SuggestionOperations::clearSuggestions))));

        // namespace remove namespace
        dispatcher.register(root
                .then(subCommand
                .then(RemoveNode
                .then(NamespaceNode
                .executes(SuggestionOperations::removeNamespace)))));

    }
}
