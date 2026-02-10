package com.github.breadbyte.itemcustomizer.server.commands.registrar;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.LoreCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.PermissionCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistrar {
    // Setup
    PermissionCommand permissionCommand = new PermissionCommand();
    RenameCommand renameCommand = new RenameCommand();
    LoreCommand loreCommand = new LoreCommand();

    ModelApplyCommand modelApplyCommand = new ModelApplyCommand();
    ModelGlintCommand modelGlintCommand = new ModelGlintCommand();
    ModelTintCommand modelTintCommand = new ModelTintCommand();
    ModelNamespaceCommand modelNamespaceCommand = new ModelNamespaceCommand();
    ModelWearCommand modelWearCommand = new ModelWearCommand();
    ModelDyeCommand modelDyeCommand = new ModelDyeCommand();

    Check.Permission modelsPermission = Check.Permission.CUSTOMIZE;

    // Register to Brigadier
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        permissionCommand.register(Check.Permission.GRANT, "permission", dispatcher, root);
        renameCommand.register(Check.Permission.RENAME, "name", dispatcher, root);
        loreCommand.register(Check.Permission.LORE,"lore", dispatcher, root);
        modelNamespaceCommand.register(Check.Permission.ADMIN, "namespace", dispatcher, root);


        // internally, model _is_ the root command, so we don't use the subCommandName
        modelApplyCommand.register(modelsPermission, "", dispatcher, root);
        modelGlintCommand.register(modelsPermission, "", dispatcher, root);
        modelTintCommand.register(modelsPermission, "", dispatcher, root);
        modelWearCommand.register(modelsPermission, "", dispatcher, root);
        modelDyeCommand.register(modelsPermission, "", dispatcher, root);
    }
}
