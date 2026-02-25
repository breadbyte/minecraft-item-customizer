package com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.ModelLockParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelLoreOperations {
    Result<String> apply(ModelLoreParams params);
    Result<String> reset(ModelLoreParams params);
}
