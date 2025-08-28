package com.github.breadbyte.itemcustomizer.server.data;

import org.jetbrains.annotations.NotNull;

public record NamespaceCategory(String namespace, String category) {
    public @NotNull String toString() {
        return namespace + "." + category;
    }

    public @NotNull String getPermissionNode() {
        return namespace + "." + category;
    }
}
