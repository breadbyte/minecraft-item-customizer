package com.github.breadbyte.itemcustomizer.server.commands.runner;

import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public interface Executor<T> {
    Result<?> run(T params);
}
