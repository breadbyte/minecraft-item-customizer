package com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock;

import net.minecraft.item.ItemStack;

public record ModelLockParams(ItemStack item, String uuid) { }
