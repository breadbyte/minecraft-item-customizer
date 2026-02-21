package com.github.breadbyte.itemcustomizer.server.util;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public class Check {
    public enum Permission {
        GRANT("itemcustomizer.grant"),
        CUSTOMIZE("itemcustomizer.customize"),
        RENAME("itemcustomizer.rename"),
        LORE("itemcustomizer.lore"),
        ADMIN("itemcustomizer.admin");

        private final String permission;

        Permission(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }
        public String getPermissionForNamespace(String namespace) {
            return this.getPermission() + "." + namespace;
        }

        public String chain(String node) {
            return this.getPermission() + "." + node;
        }
        public String chain(String... node) { return chain(String.join(".", node)); }

        public boolean checkPermissionForModel(ServerPlayerEntity player, CustomModelDefinition model) {
            ItemCustomizer.LOGGER.info("Checking permission for selector: {}", chain(model.getPermissionNode()));
            return Permissions.check(player, chain(model.getPermissionNode()));
        }
    }


    public static boolean checkPermissionForUser(ServerPlayerEntity player, String node) {
        ItemCustomizer.LOGGER.info("Checking permission for selector: {}", node);
        return Permissions.check(player, node);
    }

    public static boolean IsCreativeMode(ServerPlayerEntity player) {
        return player.isCreative();
    }

}
