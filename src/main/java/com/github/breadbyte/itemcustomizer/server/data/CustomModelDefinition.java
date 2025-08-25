package com.github.breadbyte.itemcustomizer.server.data;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class CustomModelDefinition {
    @SerialEntry
    public final String namespace;
    @SerialEntry
    public final String itemType;
    @SerialEntry
    public final String itemName;
    @SerialEntry
    public final String destination;

    public CustomModelDefinition(String namespace, String itemType, String itemName, String destination) {
        this.namespace = namespace;
        this.itemType = itemType;
        this.itemName = itemName;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return namespace + ":" + destination;
    }

    public String getNamespace() { return namespace; }
    public String getItemType() { return itemType; }
    public String getItemName() { return itemName; }
    public String getDestination() { return destination; }
    public String getPermissionNode() { return namespace + "." + destination.replace("/", "."); }

    @Override
    public boolean equals(Object obj) {
        // Sanity checks
        if (this == obj) return true;
        if (!(obj instanceof CustomModelDefinition that)) return false;

        // Compare fields
        return namespace.equals(that.namespace) &&
               itemType.equals(that.itemType) &&
               itemName.equals(that.itemName) &&
               destination.equals(that.destination);
    }
}
