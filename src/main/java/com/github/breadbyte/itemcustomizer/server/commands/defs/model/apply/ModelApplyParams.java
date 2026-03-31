package com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply;

import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import net.minecraft.item.ItemStack;

public record ModelApplyParams(ItemStack item, ModelPath identifier, CustomModelDefinition model) {}
