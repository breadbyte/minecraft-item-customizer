package com.github.breadbyte.itemcustomizer.server.commands.defs.model.rename;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelRenameOperations {
    Result<String> apply(ModelRenameParams params);
    Result<String> reset(ModelRenameParams params);
}
