package com.github.breadbyte.itemcustomizer.server.data;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * Represents a path to a model, consisting of a namespace, a category path, a subpath, and an item name.
 * Format: [namespace]:[category/subPath/itemName]
 * All segments of the path are visible to the end user and subject to permission checks.
 */
public record ModelPath(@NotNull String namespace, @NotNull String category, @NotNull String subPath, @NotNull String itemName) {

    public ModelPath {
        Objects.requireNonNull(namespace, "namespace cannot be null");
        Objects.requireNonNull(category, "category cannot be null");
        Objects.requireNonNull(subPath, "subPath cannot be null");
        Objects.requireNonNull(itemName, "itemName cannot be null");

        namespace = namespace.trim().toLowerCase();
        category = trimTrailingSlash(category.trim());
        subPath = trimLeadingSlash(trimTrailingSlash(subPath.trim()));
        itemName = itemName.trim();
    }

    /**
     * Creates a ModelPath representing a category (with an empty itemName).
     */
    public ModelPath(String namespace, String category) {
        this(namespace, category, "", "");
    }

    /**
     * Creates a ModelPath without a hidden subPath.
     */
    public ModelPath(String namespace, String category, String itemName) {
        this(namespace, category, "", itemName);
    }

    /**
     * Creates a ModelPath from its components and a destination path that may contain subpaths.
     * The destination path always starts with the category and ends with the itemName.
     * The parts in between are treated as the subPath.
     */
    public static @NotNull ModelPath fromDestination(@NotNull String namespace, @NotNull String category, @NotNull String itemName, String destination) {
        if (destination == null || destination.isBlank()) {
            return new ModelPath(namespace, category, "", itemName);
        }

        String cleanDest = trimLeadingSlash(trimTrailingSlash(destination.trim()));
        String subPath = cleanDest;

        // Remove category from the start
        if (!category.isEmpty()) {
            if (subPath.startsWith(category + "/")) {
                subPath = subPath.substring(category.length() + 1);
            } else if (subPath.equals(category)) {
                subPath = "";
            }
        }

        // Remove itemName from the end
        if (!itemName.isEmpty()) {
            if (subPath.endsWith("/" + itemName)) {
                subPath = subPath.substring(0, subPath.length() - itemName.length() - 1);
            } else if (subPath.equals(itemName)) {
                subPath = "";
            }
        }

        return new ModelPath(namespace, category, subPath, itemName);
    }

    /**
     * Parses a string of format "namespace:category/item" or "item".
     * If no namespace is present, "minecraft" is assumed.
     */
    public static @NotNull ModelPath of(String fullId) {
        if (fullId == null || fullId.isBlank()) return new ModelPath("minecraft", "", "", "");
        
        String namespace = "minecraft";
        String path = fullId;

        if (fullId.contains(":")) {
            String[] parts = fullId.split(":", 2);
            namespace = parts[0];
            path = parts[1];
        }

        return fromNamespaceAndPath(namespace, path);
    }

    /**
     * Creates a ModelPath from a namespace and a full path string.
     * The last segment of the path is treated as the item name, and everything before it is the category.
     * subPath is left empty.
     */
    public static @NotNull ModelPath fromNamespaceAndPath(String namespace, String path) {
        if (path == null) return new ModelPath(namespace, "", "", "");
        
        path = trimLeadingSlash(trimTrailingSlash(path.trim()));
        
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            return new ModelPath(namespace, "", "", path);
        } else {
            return new ModelPath(namespace, path.substring(0, lastSlash), "", path.substring(lastSlash + 1));
        }
    }

    private static String trimTrailingSlash(String str) {
        String temp = str;
        while (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp;
    }

    private static String trimLeadingSlash(String str) {
        String temp = str;
        while (temp.startsWith("/")) {
            temp = temp.substring(1);
        }
        return temp;
    }

    @Override
    public @NotNull String toString() {
        return namespace + ":" + getVisiblePath();
    }

    /**
     * Returns the full path representation including namespace and subpaths.
     */
    public @NotNull String toFullId() {
        return namespace + ":" + getFullPath();
    }

    public @NotNull String getFullPath() {
        String path = getFullCategoryPath();
        if (!itemName.isEmpty()) {
            path = path.isEmpty() ? itemName : path + "/" + itemName;
        }
        return path;
    }

    /**
     * Returns the combined category and subPath.
     */
    public @NotNull String getFullCategoryPath() {
        if (category.isEmpty()) return subPath;
        if (subPath.isEmpty()) return category;
        return category + "/" + subPath;
    }

    public @NotNull String getVisiblePath() {
        return getFullPath();
    }

    public @NotNull String getPermissionNode() {
        String fullCategory = getFullCategoryPath();
        if (fullCategory.isEmpty()) return namespace;
        return namespace + "." + fullCategory.replace("/", ".");
    }

    public @NotNull String getItemPermissionNode() {
        String base = getPermissionNode();
        return itemName.isEmpty() ? base : base + "." + itemName;
    }

    public @NotNull String withItemNamePermissionNode(String otherItemName) {
        return getPermissionNode() + "." + otherItemName;
    }

    public @NotNull String categoryWithItemName(String otherItemName) {
        String fullCategory = getFullCategoryPath();
        if (fullCategory.isEmpty()) return otherItemName;
        return fullCategory + "/" + otherItemName;
    }

    public ModelPath appendCategory(String categoryToAppend) {
        String newCategory = category.isEmpty() ? categoryToAppend : category + "/" + categoryToAppend;
        return new ModelPath(namespace, newCategory, subPath, itemName);
    }

    // Getters for compatibility
    public String getNamespace() { return namespace; }
    public String getCategory() { return category; }
    public String getSubPath() { return subPath; }
    public String getItemName() { return itemName; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ModelPath that)) return false;
        return namespace.equalsIgnoreCase(that.namespace) &&
               getFullCategoryPath().equalsIgnoreCase(that.getFullCategoryPath()) &&
               itemName.equalsIgnoreCase(that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace.toLowerCase(), getFullCategoryPath().toLowerCase(), itemName.toLowerCase());
    }
}
