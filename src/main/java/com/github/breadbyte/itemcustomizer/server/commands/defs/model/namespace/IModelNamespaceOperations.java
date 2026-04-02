package com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelNamespaceOperations {
    Result<String> addNamespace(ModelNamespaceParams params);
    Result<String> removeNamespace(ModelNamespaceParams params);
    Result<String> clearAll(ModelNamespaceParams params);
    Result<String> refreshNamespace(ModelNamespaceParams params);
    Result<String> viewUrl(ModelNamespaceParams params);
    Result<String> clearUrl(ModelNamespaceParams params);
}
