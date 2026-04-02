package com.github.breadbyte.itemcustomizer.server.commands.defs.model.copy;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelCopyOperations {
    Result<String> copyAll(ModelCopyParams params);
    Result<String> copyName(ModelCopyParams params);
    Result<String> copyLore(ModelCopyParams params);
    Result<String> copyModel(ModelCopyParams params);
}
