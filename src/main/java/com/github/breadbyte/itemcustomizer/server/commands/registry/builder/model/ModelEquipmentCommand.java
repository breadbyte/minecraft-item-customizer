package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment.ModelEquipmentRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelEquipmentCommand implements BaseCommand {

    private static ModelEquipmentRunner RUNNER;
    public ModelEquipmentCommand(ModelEquipmentRunner runner) {
        RUNNER = runner;
    }

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var EquipmentNode = literal("equipment");
        var ResetNode = literal("reset");

        dispatcher.register(_root
                .then(EquipmentNode
                        .executes(RUNNER::setEquipmentTexture)));
        dispatcher.register(_root
                .then(EquipmentNode
                .then(ResetNode
                        .executes(RUNNER::resetEquipmentTexture))));
    }
}
