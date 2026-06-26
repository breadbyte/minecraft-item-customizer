package com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelCopyOperations {
    Result<String> copy(ModelCopyParams params);
}
