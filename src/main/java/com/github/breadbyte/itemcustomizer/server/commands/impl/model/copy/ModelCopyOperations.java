package com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.IModelCopyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy.ModelCopyParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;

public class ModelCopyOperations implements IModelCopyOperations {
    @Override
    public Result<String> copy(ModelCopyParams params) {
        var mainHand = params.mainHand();
        var offHand = params.offHand();

        mainHand.applyComponentsFrom(offHand.getComponents());

        return Result.ok("Copied properties from offhand to mainhand!");
    }
}
