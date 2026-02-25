package com.github.breadbyte.itemcustomizer.server.commands.defs.model.rename;

import net.minecraft.item.ItemStack;

public record ModelRenameParams(ItemStack item, String name) {
}
