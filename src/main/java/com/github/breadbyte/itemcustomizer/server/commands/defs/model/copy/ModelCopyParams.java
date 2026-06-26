package com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy.COPY_TO_ARGUMENT;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy.COPY_WHAT_ARGUMENT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public record ModelCopyParams(PlayerEntity player, ItemStack mainHand, ItemStack offHand, COPY_TO_ARGUMENT copyTo, COPY_WHAT_ARGUMENT copyWhat) {
}
