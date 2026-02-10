package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.server.commands.registrar.CommandRegistrar;
//import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class CommandRegistration {

    public static void RegisterCommands() {

        // This is the base name of the command.
        var root = CommandManager.literal("model");
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {

            // To add new commands, create a new class in commands/registrar/commands
            // create its implementation in commands/impl
            // and add it to the CommandRegistrar.registerCommands method

            CommandRegistrar registrar = new CommandRegistrar();
            registrar.registerCommands(dispatcher, root);
        });
    }
}
