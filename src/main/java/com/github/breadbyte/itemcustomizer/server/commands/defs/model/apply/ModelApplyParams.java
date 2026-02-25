package com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply;

import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import net.minecraft.item.ItemStack;

public record ModelApplyParams(ItemStack item, NamespaceCategory identifier, CustomModelDefinition model) {}
