package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Helper;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a path to a model, consisting of a namespace, a category path, a subpath, and an item name.
 * Format: [namespace]:[category/subPath/itemName]
 * All segments of the path are visible to the end user and subject to permission checks.
 */
public record ModelPath(@NotNull String path, @NotNull String namespace, @NotNull List<String> segments) {

    /**
     * Canonical constructor or helper can be used to parse a single string.
     * However, since records need all components at once, we use a static factory method
     * or a secondary constructor.
     */
    public static ModelPath of(@NotNull String rawPath) {
        String cleanedPath = Helper.trimTrailingSlash(rawPath);

        // 1. Extract the Namespace (everything before the first ':')
        int firstColon = cleanedPath.indexOf(':');
        String namespace = (firstColon != -1) ? cleanedPath.substring(0, firstColon) : "";

        // 2. Extract each segment from the path (excluding the namespace)
        List<String> segments = getStrings(firstColon, cleanedPath);

        return new ModelPath(cleanedPath, namespace, segments);
    }

    private static @NonNull List<String> getStrings(int firstColon, String cleanedPath) {
        List<String> segments = new ArrayList<>();
        int start = (firstColon != -1) ? firstColon + 1 : 0;
        for (int i = start; i < cleanedPath.length(); i++) {
            char c = cleanedPath.charAt(i);
            if (c == '/') {
                if (i > start) {
                    segments.add(cleanedPath.substring(start, i));
                }
                start = i + 1;
            }
        }
        if (start < cleanedPath.length()) {
            segments.add(cleanedPath.substring(start));
        }
        return segments;
    }

    @Override
    public @NotNull String toString() {
        return path;
    }

    public @NotNull String getFullPath() {
        return toString();
    }

    public @NotNull String getPermissionNode() {
        return path.replace("/", ".").replace(":", ".");
    }

    @Deprecated
    public @NotNull String getCategory() {
        // Given namespace:category/subcategory/path, this returns category

        int colonIndex = path.indexOf(':');
        int firstSlash = path.indexOf('/');

        // If there's no slash, or the first slash is part of the namespace (before the colon), there's no category
        if (firstSlash == -1 || (colonIndex != -1 && firstSlash < colonIndex)) {
            return "";
        }
        return (colonIndex != -1) ? path.substring(colonIndex + 1, firstSlash) : path.substring(0, firstSlash);
    }

    @Deprecated
    public String itemName() { return getLastSegment(); }

    public @NotNull String getLastSegment() {
        String cleanedPath = Helper.trimTrailingSlash(getFullPath());

        // 2. Extract the Last Segment (everything after the last ':' or '/')
        int lastSlash = cleanedPath.lastIndexOf('/');
        int lastColon = cleanedPath.lastIndexOf(':');
        int lastDelimiter = Math.max(lastSlash, lastColon);

        return cleanedPath.substring(lastDelimiter + 1);
    }

    public @NotNull String getSegments() {
        return String.join("/", segments);
    }
    
    public @NotNull String getSegmentToDepth(int depthZeroIndex_NotIncludingNamespace) {
        if (segments.isEmpty()) return namespace + ":";
        int limit = Math.min(depthZeroIndex_NotIncludingNamespace + 1, segments.size());
        StringBuilder sb = new StringBuilder(namespace).append(":");
        for (int i = 0; i < limit; i++) {
            sb.append(segments.get(i));
            if (i < limit - 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    // Getters for compatibility
    public String getNamespace() { return namespace; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ModelPath that)) return false;
        return path.equalsIgnoreCase(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path.toLowerCase());
    }
}
