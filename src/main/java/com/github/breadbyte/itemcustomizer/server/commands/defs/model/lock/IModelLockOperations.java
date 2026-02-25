package com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelLockOperations {
    Result<String> lock(ModelLockParams params);
    Result<String> unlock(ModelLockParams params);
}
