package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Permission;
import dev.isxander.yacl3.config.v2.api.SerialEntry;

public record CustomModelDefinition(@SerialEntry NamespaceCategory namespaceCategory, @SerialEntry String madeBy) {

    @Override
    public String toString() {
        return namespaceCategory.toString();
    }

    public String getNamespace() {
        return namespaceCategory.getNamespace();
    }

    public String getCategory() {
        return namespaceCategory.getCategory();
    }

    public String getName() {
        return namespaceCategory.itemName();
    }

    public String getPermissionNode() {
        return Permission.CUSTOMIZE.chain(namespaceCategory.getPermissionNode()).getPermission();
    }

    @Override
    public boolean equals(Object obj) {
        // Sanity checks
        if (this == obj) return true;
        if (!(obj instanceof CustomModelDefinition(NamespaceCategory category, String by))) return false;

        if (!namespaceCategory.namespace().equalsIgnoreCase(category.namespace())) return false;
        if (!namespaceCategory.category().equalsIgnoreCase(category.category())) return false;
        if (!namespaceCategory.itemName().equalsIgnoreCase(category.itemName())) return false;
        return madeBy.equals(by);
    }
}
