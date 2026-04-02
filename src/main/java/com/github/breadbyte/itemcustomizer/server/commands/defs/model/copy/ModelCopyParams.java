package com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy;

import net.minecraft.item.ItemStack;

public record ModelCopyParams(ItemStack mainHand, ItemStack offHand) {
}
