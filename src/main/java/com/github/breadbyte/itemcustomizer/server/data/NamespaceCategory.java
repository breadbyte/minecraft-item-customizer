package com.github.breadbyte.itemcustomizer.server.data;

import org.jetbrains.annotations.NotNull;

public record NamespaceCategory(String namespace, String category) {

    public NamespaceCategory {
        if (category == null) { throw new NullPointerException(); }
        namespace = namespace;
        category = TrimTrailingSlash(category);
    }

    public @NotNull String toString() {
        return namespace + ":" + category;
    }
    public @NotNull String withItemName(String itemName) {
        if (category.isBlank()) return namespace + ":" + itemName;
        return this.toString() + "/" + itemName;
    }
    public @NotNull String withItemNamePermissionNode(String itemName) {
        if (category.isBlank()) return namespace + "." + itemName;
        return namespace + "." + category.replace("/", ".") + "." + itemName;
    }
    public @NotNull String categoryWithItemName(String itemName) {
        if (category.isBlank()) return itemName;
        return category + "/" + itemName;
    }
    public @NotNull String getNamespace() { return namespace; }
    public @NotNull String getCategory() { return category; }
    public @NotNull String getPermissionNode() {
        if (category.contains("/")) {
            return namespace + "." + category.replace("/", ".");
        }
        return namespace + "." + category;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NamespaceCategory that = (NamespaceCategory) obj;
        return namespace.equalsIgnoreCase(that.namespace) && category.equalsIgnoreCase(that.category);
    }

    public static NamespaceCategory of(String namespace, String category) {
        return new NamespaceCategory(namespace, category);
    }

    public NamespaceCategory appendCategory(String categoryToAppend) {
        if (category.isBlank()) return new NamespaceCategory(namespace, categoryToAppend);
        return new NamespaceCategory(namespace, category + "/" + categoryToAppend);
    }

    private String TrimTrailingSlash(String str) {
        var temp = str;
        // Check for multiple trailing slashes and trim them
        while (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }

        return temp;
    }
}
