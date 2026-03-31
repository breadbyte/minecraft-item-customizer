package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Permission;
import dev.isxander.yacl3.config.v2.api.SerialEntry;

import java.util.Objects;

public record CustomModelDefinition(@SerialEntry ModelPath modelPath, @SerialEntry String madeBy) {

    public CustomModelDefinition {
        Objects.requireNonNull(modelPath, "modelPath cannot be null");
        Objects.requireNonNull(madeBy, "madeBy cannot be null");
    }

    @Override
    public String toString() {
        return modelPath.toString();
    }

    public String getNamespace() {
        return modelPath.namespace();
    }

    public String getCategory() {
        return modelPath.getFullCategoryPath();
    }

    public String getName() {
        return modelPath.itemName();
    }

    public Boolean __internalGetPrependCustom() {
        return modelPath.__internalPrependCustom();
    }

    public ModelPath getModelPath() { return modelPath; }

    public String getPermissionNode() {
        return Permission.CUSTOMIZE.chain(modelPath.getItemPermissionNode()).getPermission();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomModelDefinition that)) return false;
        return Objects.equals(modelPath, that.modelPath) && Objects.equals(madeBy, that.madeBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelPath, madeBy);
    }
}
