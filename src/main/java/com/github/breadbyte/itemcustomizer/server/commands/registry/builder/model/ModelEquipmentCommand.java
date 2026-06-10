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
        // The 'root' here is already the 'model' command.
        // We need to apply the permission to the 'equipment' and 'reset' subcommands.

        var EquipmentNode = InternalHelper.RequirePermissionFor(literal("equipment"), permission);
        var ResetNode = InternalHelper.RequirePermissionFor(literal("reset"), permission);

        dispatcher.register(root
                .then(EquipmentNode
                        .executes(RUNNER::setEquipmentTexture)));
        dispatcher.register(root
                .then(EquipmentNode
                .then(ResetNode
                        .executes(RUNNER::resetEquipmentTexture))));
    }
}