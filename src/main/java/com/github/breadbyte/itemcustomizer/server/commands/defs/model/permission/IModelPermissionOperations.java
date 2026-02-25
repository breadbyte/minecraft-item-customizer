package com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission;

import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.entity.player.PlayerEntity;

public interface IModelPermissionOperations {
    Result<String> grantPermission(ModelPermissionParams params);
    Result<String> revokePermission(ModelPermissionParams params);
    Result<String> getPermissionNode(ModelPermissionParams params);
}
