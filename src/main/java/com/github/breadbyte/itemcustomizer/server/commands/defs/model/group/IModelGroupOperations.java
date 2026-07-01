package com.github.breadbyte.itemcustomizer.server.commands.defs.model.group;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelGroupOperations {
    Result<String> addGroup(ModelGroupParams params);
    Result<String> removeGroup(ModelGroupParams params);
    Result<String> listGroup(ModelGroupParams params);
    Result<String> promoteAdmin(ModelGroupParams params);
    Result<String> demoteAdmin(ModelGroupParams params);
    Result<String> addToGroup(ModelGroupParams params);
    Result<String> removeFromGroup(ModelGroupParams params);
    Result<String> lockToGroup(ModelGroupParams params);
    Result<String> unlockFromGroup(ModelGroupParams params);
}
