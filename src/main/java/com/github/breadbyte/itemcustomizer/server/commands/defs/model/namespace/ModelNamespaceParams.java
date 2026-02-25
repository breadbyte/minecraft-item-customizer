package com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace;

import net.minecraft.server.MinecraftServer;

public record ModelNamespaceParams(String namespace, String url, MinecraftServer server,
                                   net.minecraft.server.command.ServerCommandSource source) {
}
