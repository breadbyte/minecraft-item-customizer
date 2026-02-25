package com.github.breadbyte.itemcustomizer.server.commands.defs.model.equipment;

import com.github.breadbyte.itemcustomizer.server.util.Result;

public interface IModelEquipmentOperations {
    Result<String> toggle(ModelEquipmentParams params);
    Result<String> reset(ModelEquipmentParams params);
}
