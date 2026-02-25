package com.github.breadbyte.itemcustomizer.server.commands.defs.model.glint;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelGlintOperations {
    Result<String> toggle(ModelGlintParams params);
}
