package com.github.breadbyte.itemcustomizer.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

// S in this context is ServerCommandSource
public interface CommandDefinition<ServerCommandSource> {
    String commandName();
    void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root);
}
