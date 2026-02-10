package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.server.commands.impl.GrantCommands;
import com.github.breadbyte.itemcustomizer.server.commands.impl.ModelCommands;
import com.github.breadbyte.itemcustomizer.server.commands.impl.LoreCommands;
import com.github.breadbyte.itemcustomizer.server.commands.impl.RenameCommands;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.CommandRegistrar;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.PermissionCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.RenameCommand;
import com.github.breadbyte.itemcustomizer.server.operations.*;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelCategorySuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.suggester.ModelSuggestionProvider;
import com.github.breadbyte.itemcustomizer.server.suggester.NamespaceSuggestionProvider;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
//import me.lucko.fabric.api.permissions.v0.Permissions;
import com.mojang.brigadier.suggestion.Suggestions;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;

import java.util.Objects;

public class CommandRegistration {

    public static void RegisterCommands() {

        // This is the base name of the command.
        var root = CommandManager.literal("model2");
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {

            // To add new commands, create a new class in commands/registrar/commands
            // create its implementation in commands/impl
            // and add it to the CommandRegistrar.registerCommands method

            CommandRegistrar registrar = new CommandRegistrar();
            registrar.registerCommands(dispatcher, root);
        });
    }
}
