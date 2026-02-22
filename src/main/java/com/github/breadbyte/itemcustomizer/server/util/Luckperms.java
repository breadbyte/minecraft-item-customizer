package com.github.breadbyte.itemcustomizer.server.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.node.Node;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.network.ServerPlayerEntity;

public class Luckperms {
    public static boolean IsLuckpermsPresent() {
        var serve = FabricLoader.getInstance().getModContainer("luckperms");
        return serve.isPresent();
    }

    public static boolean CheckPermission(ServerPlayerEntity player, String node) {
        if (!IsLuckpermsPresent()) return false;

        LuckPerms l = LuckPermsProvider.get();

        PlayerAdapter<ServerPlayerEntity> adapter = l.getPlayerAdapter(ServerPlayerEntity.class);
        CachedPermissionData permissionData = adapter.getPermissionData(player);

        Tristate checkResult = permissionData.checkPermission(node);
        return checkResult.asBoolean();
    }

    public static boolean GrantPermission(ServerPlayerEntity player, String node) {
        if (!IsLuckpermsPresent()) return false;

        LuckPerms l = LuckPermsProvider.get();
        l.getUserManager().loadUser(player.getUuid()).thenAccept(user -> {
            user.data().add(Node.builder(node).build());
            l.getUserManager().saveUser(user);
        });
        return true;
    }
}
