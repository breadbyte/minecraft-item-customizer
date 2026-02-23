package com.github.breadbyte.itemcustomizer.server.commands.registrar;

import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.LoreCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelPermissionCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.*;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistrar {
    // Setup
    ModelPermissionCommand modelPermissionCommand = new ModelPermissionCommand();
    RenameCommand renameCommand = new RenameCommand();
    LoreCommand loreCommand = new LoreCommand();

    ModelApplyCommand modelApplyCommand = new ModelApplyCommand();
    ModelGlintCommand modelGlintCommand = new ModelGlintCommand();
    ModelTintCommand modelTintCommand = new ModelTintCommand();
    ModelNamespaceCommand modelNamespaceCommand = new ModelNamespaceCommand();
    ModelWearCommand modelWearCommand = new ModelWearCommand();
    ModelDyeCommand modelDyeCommand = new ModelDyeCommand();
    ModelLockCommand modelLockCommand = new ModelLockCommand();

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
    }
}
