package com.github.breadbyte.itemcustomizer.server.commands.defs.model.tint;

import net.minecraft.item.ItemStack;

public record ModelTintParams(ItemStack item, Integer tintIndex, Integer tintColor) {
}
