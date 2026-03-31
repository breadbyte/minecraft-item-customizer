package com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace;

import net.minecraft.server.MinecraftServer;

import java.net.URL;

public record ModelNamespaceParams(String namespace, URL url, MinecraftServer server,
                                   net.minecraft.server.command.ServerCommandSource source) {
}
