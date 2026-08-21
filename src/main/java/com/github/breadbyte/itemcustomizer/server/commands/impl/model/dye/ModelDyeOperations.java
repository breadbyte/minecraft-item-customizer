package com.github.breadbyte.itemcustomizer.server.commands.impl.model.dye;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye.IModelDyeOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye.ModelDyeParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;

public class ModelDyeOperations implements IModelDyeOperations {

    @Override
    public Result<String> apply(ModelDyeParams params) {
        var item = params.item();

        var nbt = item.getOrCreateNbt();

        var displayTag = nbt.getCompound("display");
        if (!nbt.contains("display")) {
            nbt.put("display", displayTag);
        }

        int rgbDecimalColor = params.color();
        displayTag.putInt("color", rgbDecimalColor);

        return Result.ok();
    }

    @Override
    public Result<String> reset(ModelDyeParams params) {
        var item = params.item();
        var nbt = item.getOrCreateNbt();

        // check if display exists
        // from display { color }
        var displayTag = nbt.getCompound("display");
        if (!nbt.contains("display")) {
            return Result.ok();
        }

        // remove color
        displayTag.remove("color");
        return Result.ok();
    }
}
