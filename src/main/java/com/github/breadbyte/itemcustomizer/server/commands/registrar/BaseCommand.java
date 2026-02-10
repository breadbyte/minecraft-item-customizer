package com.github.breadbyte.itemcustomizer.server.commands.registrar;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public interface BaseCommand {
    void register(Check.Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root);
}
