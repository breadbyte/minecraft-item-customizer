package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Check;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public class CustomModelDefinition {
    @SerialEntry
    public final String namespace;
    @SerialEntry
    public final String category;
    @SerialEntry
    public final String name;
    @SerialEntry
    public final String madeBy;

    public CustomModelDefinition(String namespace, String category, String name, String madeBy) {
        // Trim trailing slashes
        this.category = category.trim().endsWith("/") ? category.trim().substring(0, category.trim().length() - 1) : category.trim();
        this.namespace = namespace;
        this.name = name;
        this.madeBy = madeBy;
    }

    public CustomModelDefinition(NamespaceCategory namespaceCategory, String name, String madeBy) {
        this(namespaceCategory.getNamespace(), namespaceCategory.getCategory(), name, madeBy);
    }

    @Override
    public String toString() { return namespace + ":" + category + "/" + name; }
    public String getNamespace() { return namespace; }
    public String getCategory() { return category; }
    public String getName() { return name; }

    public String getMadeBy() { return madeBy; }
    public NamespaceCategory getNamespaceCategory() { return new NamespaceCategory(namespace, category); }
    public String getPermissionNode() { return namespace + "." + category.replace("/", "."); }

    public boolean getPermission(ServerPlayerEntity player) {
        return Permissions.check(player, Check.Permission.CUSTOMIZE.chain(getPermissionNode()));
    }

    @Override
    public boolean equals(Object obj) {
        // Sanity checks
        if (this == obj) return true;
        if (!(obj instanceof CustomModelDefinition that)) return false;

        // Quick namespace check
        if (!namespace.equals(that.namespace)) return false;

        // Compare fields
        return category.equals(that.category)
                && name.equals(that.name);
    }
}
