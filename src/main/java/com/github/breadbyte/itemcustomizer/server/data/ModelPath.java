package com.github.breadbyte.itemcustomizer.server.data;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

/**
 * Represents a path to a model, consisting of a namespace, a category path, an optional hidden subpath, and an item name.
 * Format: [namespace]:[category/subPath/itemName]
 * Only namespace, category, and itemName are visible to the end user.
 */
public record ModelPath(@NotNull String namespace, @NotNull String category, @NotNull String subPath, @NotNull String itemName, @NotNull Boolean __internalPrependCustom) {

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
        this(namespace, category, "", "", false);
    }

    /**
     * Creates a ModelPath without a hidden subPath.
     */
    public ModelPath(String namespace, String category, String itemName) {
        this(namespace, category, "", itemName, false);
    }

    /**
     * Creates a ModelPath from its components and a destination path that may contain hidden subpaths.
     * The destination path always starts with the category and ends with the itemName.
     * The parts in between are treated as the hidden subPath.
     */
    public static @NotNull ModelPath fromDestination(@NotNull String namespace, @NotNull String category, @NotNull String itemName, String destination, Boolean __internalPrependCustom) {
        if (destination == null || destination.isBlank()) {
            return new ModelPath(namespace, category, "", itemName, __internalPrependCustom);
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

        return new ModelPath(namespace, category, subPath, itemName, __internalPrependCustom);
    }

    /**
     * Parses a string of format "namespace:category/item" or "item".
     * If no namespace is present, "minecraft" is assumed.
     * Note: This does not support parsing hidden subpaths from a string.
     */
    public static @NotNull ModelPath of(String fullId) {
        if (fullId == null || fullId.isBlank()) return new ModelPath("minecraft", "", "", "", false);
        
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
        if (path == null) return new ModelPath(namespace, "", "", "", false);
        
        path = trimLeadingSlash(trimTrailingSlash(path.trim()));
        
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            return new ModelPath(namespace, "", "", path, false);
        } else {
            return new ModelPath(namespace, path.substring(0, lastSlash), "", path.substring(lastSlash + 1), false);
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
     * Returns the full path representation including namespace and hidden subpaths.
     */
    public @NotNull String toFullId() {
        return namespace + ":" + getFullPath();
    }

    public @NotNull String getFullPath() {
        String path = category;
        if (!subPath.isEmpty()) {
            path = path.isEmpty() ? subPath : path + "/" + subPath;
        }
        if (!itemName.isEmpty()) {
            path = path.isEmpty() ? itemName : path + "/" + itemName;
        }
        return path;
    }

    public @NotNull String getVisiblePath() {
        if (category.isEmpty()) return itemName;
        if (itemName.isEmpty()) return category;
        return category + "/" + itemName;
    }

    public @NotNull String getPermissionNode() {
        if (category.isEmpty()) return namespace;
        return namespace + "." + category.replace("/", ".");
    }

    public @NotNull String getItemPermissionNode() {
        String base = getPermissionNode();
        return itemName.isEmpty() ? base : base + "." + itemName;
    }

    public @NotNull String withItemNamePermissionNode(String otherItemName) {
        return getPermissionNode() + "." + otherItemName;
    }

    public @NotNull String categoryWithItemName(String otherItemName) {
        if (category.isEmpty()) return otherItemName;
        return category + "/" + otherItemName;
    }

    public ModelPath appendCategory(String categoryToAppend) {
        String newCategory = category.isEmpty() ? categoryToAppend : category + "/" + categoryToAppend;
        return new ModelPath(namespace, newCategory, subPath, itemName, false);
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
               category.equalsIgnoreCase(that.category) &&
               subPath.equalsIgnoreCase(that.subPath) &&
               itemName.equalsIgnoreCase(that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace.toLowerCase(), category.toLowerCase(), subPath.toLowerCase(), itemName.toLowerCase());
    }
}
