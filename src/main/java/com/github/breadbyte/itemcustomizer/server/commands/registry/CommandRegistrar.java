package com.github.breadbyte.itemcustomizer.server.commands.registry;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply.ModelApplyAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply.ModelApplyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply.ModelApplyRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.dye.ModelDyeAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.dye.ModelDyeOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.dye.ModelDyeRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment.ModelEquipmentAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment.ModelEquipmentOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.equipment.ModelEquipmentRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint.ModelGlintAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint.ModelGlintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.glint.ModelGlintRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock.ModelLockAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock.ModelLockOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lock.ModelLockRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore.ModelLoreRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace.ModelNamespaceAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace.ModelNamespaceOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace.ModelNamespaceRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission.ModelPermissionAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission.ModelPermissionOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission.ModelPermissionRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.rename.ModelRenameAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.rename.ModelRenameOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.rename.ModelRenameRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.tint.ModelTintAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.tint.ModelTintOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.tint.ModelTintRunner;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear.ModelWearAdapter;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear.ModelWearOperations;
import com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear.ModelWearRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.LoreCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelPermissionCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.*;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistrar {
    // Setup
    ModelPermissionCommand modelPermissionCommand = new ModelPermissionCommand(new ModelPermissionRunner(new ModelPermissionAdapter(), new ModelPermissionOperations()));
    RenameCommand renameCommand = new RenameCommand(new ModelRenameRunner(new ModelRenameAdapter(), new ModelRenameOperations()));
    LoreCommand loreCommand = new LoreCommand(new ModelLoreRunner(new ModelLoreAdapter(), new ModelLoreOperations()));

    ModelApplyCommand modelApplyCommand = new ModelApplyCommand(new ModelApplyRunner(new ModelApplyAdapter(), new ModelApplyOperations()));
    ModelGlintCommand modelGlintCommand = new ModelGlintCommand(new ModelGlintRunner(new ModelGlintAdapter(), new ModelGlintOperations()));
    ModelTintCommand modelTintCommand = new ModelTintCommand(new ModelTintRunner(new ModelTintAdapter(), new ModelTintOperations()));
    ModelNamespaceCommand modelNamespaceCommand = new ModelNamespaceCommand(new ModelNamespaceRunner(new ModelNamespaceAdapter(), new ModelNamespaceOperations()));
    ModelWearCommand modelWearCommand = new ModelWearCommand(new ModelWearRunner(new ModelWearAdapter(), new ModelWearOperations()));
    ModelDyeCommand modelDyeCommand = new ModelDyeCommand(new ModelDyeRunner(new ModelDyeAdapter(), new ModelDyeOperations()));
    ModelLockCommand modelLockCommand = new ModelLockCommand(new ModelLockRunner(new ModelLockAdapter(), new ModelLockOperations()));
    ModelEquipmentCommand modelEquipmentCommand = new ModelEquipmentCommand(new ModelEquipmentRunner(new ModelEquipmentAdapter(), new ModelEquipmentOperations()));

    Permission modelsPermission = Permission.CUSTOMIZE;

    // Register to Brigadier
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        renameCommand.register(Permission.RENAME, "name", dispatcher, root);
        loreCommand.register(Permission.LORE,"lore", dispatcher, root);

        modelPermissionCommand.register(Permission.GRANT, "permission", dispatcher, root);
        modelNamespaceCommand.register(Permission.ADMIN, "namespace", dispatcher, root);

        // internally, model _is_ the root command, so we don't use the subCommandName
        modelApplyCommand.register(modelsPermission, "", dispatcher, root);
        modelGlintCommand.register(modelsPermission, "", dispatcher, root);
        modelTintCommand.register(modelsPermission, "", dispatcher, root);
        modelWearCommand.register(modelsPermission, "", dispatcher, root);
        modelDyeCommand.register(modelsPermission, "", dispatcher, root);
        modelLockCommand.register(modelsPermission, "", dispatcher, root);
        modelEquipmentCommand.register(modelsPermission, "", dispatcher, root);
    }
}
