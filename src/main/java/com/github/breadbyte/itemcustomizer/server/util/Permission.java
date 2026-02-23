package com.github.breadbyte.itemcustomizer.server.util;

import org.jetbrains.annotations.NotNull;

public record Permission(String permission) {
    public static final Permission BASE = new Permission("itemcustomizer");
    public static final Permission GRANT = BASE.chain("grant");
    public static final Permission CUSTOMIZE = BASE.chain("customize");
    public static final Permission RENAME = BASE.chain("rename");
    public static final Permission LORE = BASE.chain("lore");
    public static final Permission ADMIN = BASE.chain("admin");


    public String getPermission() {
        return permission;
    }

    public Permission chain(String node) {
        return new Permission(this.permission + "." + node);
    }

    public Permission chain(String... nodes) {
        if (nodes.length == 0) return this;
        return new Permission(this.permission + "." + String.join(".", nodes));
    }

    @Override
    public @NotNull String toString() {
        return getPermission();
    }
}



