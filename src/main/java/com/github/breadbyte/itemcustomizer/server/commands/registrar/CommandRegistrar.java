package com.github.breadbyte.itemcustomizer.server.commands.registrar;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.commands.CommandDefinition;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashSet;
import java.util.Set;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public final class CommandRegistrar<S> {
    private CommandDispatcher<ServerCommandSource> dispatcher;
    private final Set<String> registered = new HashSet<String>();
    private final LiteralArgumentBuilder<ServerCommandSource> rootNode = literal("model");

    public CommandRegistrar(CommandDispatcher<ServerCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void register(CommandDefinition<ServerCommandSource> command) {
        if (dispatcher == null)
            throw new IllegalArgumentException("command dispatcher cannot be null");

        var name = command.commandName();

        if (registered.contains(name)) {
            return; // Prevent re-registering already registered commands
        }

        ItemCustomizer.LOGGER.info("Registering {} ...", name);
        command.register(dispatcher, rootNode);
        registered.add(name);
    }

    public void setDispatcher(CommandDispatcher<ServerCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    };
}
