package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace.ModelNamespaceRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelNamespaceCommand implements BaseCommand {

    private static ModelNamespaceRunner RUNNER;
    public ModelNamespaceCommand(ModelNamespaceRunner runner) {
        RUNNER = runner;
    }

    public static final String NAMESPACE_ARGUMENT = "namespace";
    public static final String URL_ARGUMENT = "url";

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
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
                .executes(RUNNER::addNamespace))))));

        // namespace clear
        dispatcher.register(root
                .then(subCommand
                .then(ClearNode
                .executes(RUNNER::clearAll))));

        // namespace remove namespace
        dispatcher.register(root
                .then(subCommand
                .then(RemoveNode
                .then(NamespaceNode
                .executes(RUNNER::removeNamespace)))));

    }
}
