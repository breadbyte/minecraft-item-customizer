package com.github.breadbyte.itemcustomizer.server.commands.defs.model.wear;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelWearOperations {
    Result<String> apply(ModelWearParams params);
}
