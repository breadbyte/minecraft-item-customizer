package com.github.breadbyte.itemcustomizer.server.commands.defs.model.group;

import net.minecraft.server.network.ServerPlayerEntity;

public record ModelGroupParams(String groupName, ServerPlayerEntity sourcePlayer, ServerPlayerEntity targetPlayer) { }
