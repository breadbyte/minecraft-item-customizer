package com.github.breadbyte.itemcustomizer.server.commands.defs.model.dye;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelDyeOperations {
    Result<String> apply(ModelDyeParams params);
    Result<String> reset(ModelDyeParams params);
}
