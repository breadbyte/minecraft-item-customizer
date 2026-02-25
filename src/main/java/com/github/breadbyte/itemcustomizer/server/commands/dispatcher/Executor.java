package com.github.breadbyte.itemcustomizer.server.commands.dispatcher;

import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface Executor {
    Result<?> apply(PlayerEntity player, CommandContext<ServerCommandSource> ctx);
}
