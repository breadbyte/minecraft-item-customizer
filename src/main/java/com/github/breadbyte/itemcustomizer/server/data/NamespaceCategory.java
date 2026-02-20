package com.github.breadbyte.itemcustomizer.server.data;

import org.jetbrains.annotations.NotNull;

public record NamespaceCategory(String namespace, String category) {
    public @NotNull String toString() {
        return namespace + "." + category;
    }
    public @NotNull String getNamespace() { return namespace; }
    public @NotNull String getCategory() { return category; }
    public @NotNull String getPermissionNode() {
        return namespace + "." + category;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NamespaceCategory that = (NamespaceCategory) obj;
        return namespace.equalsIgnoreCase(that.namespace) && category.equalsIgnoreCase(that.category);
    }
}
