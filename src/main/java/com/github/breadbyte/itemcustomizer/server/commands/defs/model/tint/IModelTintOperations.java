package com.github.breadbyte.itemcustomizer.server.commands.defs.model.tint;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelTintOperations {

    Result<String> apply(ModelTintParams params);
    Result<String> reset(ModelTintParams params);
}
