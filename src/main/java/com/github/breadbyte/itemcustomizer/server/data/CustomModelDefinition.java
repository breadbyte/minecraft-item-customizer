package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
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
    public final String destination;
    @SerialEntry
    public final String madeBy;

    public CustomModelDefinition(String namespace, String category, String name, String destination, String madeBy) {
        this.namespace = namespace;
        this.category = category;
        this.name = name;
        this.destination = destination;
        this.madeBy = madeBy;
    }

    @Override
    public String toString() { return namespace; }

    public String getNamespace() { return namespace; }
    public String getCategory() { return category; }
    public String getName() { return name; }
    public String getDestination() { return destination; }
    public String getMadeBy() { return madeBy; }
    public String getNamespaceCategoryPermissionNode() { return namespace + "." + category; }
    public String getPermissionNode() { return namespace + "." + destination.replace("/", "."); }

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
                && name.equals(that.name)
                && destination.equals(that.destination);
    }
}
