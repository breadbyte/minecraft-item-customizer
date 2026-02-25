package com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply;

import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.item.ItemStack;

public interface IModelApplyOperations {
    Result<String> apply(ModelApplyParams params);
    Result<String> reset(ModelApplyParams params);
}
