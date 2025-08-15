package com.github.breadbyte.itemcustomizer.client;

import com.github.breadbyte.itemcustomizer.server.ModelOperations;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class CommandRegistration {
    public static void RegisterCommands() {
        /*ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    ClientCommandManager.literal("model")
                            .then(ClientCommandManager.literal("apply")
                                    .then(ClientCommandManager.argument("namespace", StringArgumentType.word())
                                            .then(ClientCommandManager.argument("path", StringArgumentType.greedyString())
                                                    .executes(Model::Apply))))
                            //.then(ClientCommandManager.literal("reset")
                                    //.executes(ModelOperations::revertModel))
            );
        });*/

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("model")
                            .then(CommandManager.literal("apply")
                                    .then(CommandManager.argument("namespace", StringArgumentType.word())
                                            .then(CommandManager.argument("path", StringArgumentType.greedyString())
                                                    .executes(ModelOperations::applyModel))))
                            .then(CommandManager.literal("reset")
                                    .executes(ModelOperations::revertModel))
            );
        });
    }
}
