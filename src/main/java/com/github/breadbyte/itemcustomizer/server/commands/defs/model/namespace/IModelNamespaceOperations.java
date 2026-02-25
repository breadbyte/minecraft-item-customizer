package com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelNamespaceOperations {
    Result<String> addNamespace(ModelNamespaceParams params);
    Result<String> removeNamespace(ModelNamespaceParams params);
    Result<String> clearAll(ModelNamespaceParams params);
}
