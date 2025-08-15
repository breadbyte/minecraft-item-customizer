package com.github.breadbyte.itemcustomizer.server;

import com.mojang.brigadier.arguments.StringArgumentType;
//import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class CommandRegistration {

    public static void RegisterCommands() {
        // TODO: shiny item (item shine)
        // TODO: item rarity (item name color)
        // Register commands

        // This registers the following commands:
        //
        // model apply <namespace> <path> - Applies a model to the item in the player's hand.
        // Usage:
        // /model apply minecraft stone - Applies the minecraft:stone model to the item in the player's hand.
        //
        // model reset - Resets the model of the item in the player's hand, and gives them 1 experience level in return.
        // Usage:
        // /model reset
        //
        // model reset force - Resets the model of the item in the player's hand, even if it is the default model.
        // Usage:
        // /model reset force
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("model")
                            //.requires(Permissions.require(Check.Permission.CUSTOMIZE.getPermission()))
                            .then(CommandManager.literal("apply")
                                    .then(CommandManager.argument("namespace", StringArgumentType.word())
                                            .then(CommandManager.argument("path", StringArgumentType.greedyString())
                                                    .executes(ModelOperations::applyModel))))
                            .then(CommandManager.literal("reset")
                                    .executes(ModelOperations::revertModel))
            );
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("rename")
                            //.requires(Permissions.require(Check.Permission.RENAME.getPermission()))
                            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                    .executes(RenameOperations::renameItem))
                            .then(CommandManager.literal("reset")
                                    .executes(RenameOperations::resetName))
                            .then(CommandManager.literal("help")
                                    .executes(HelpOperations::RenameHelp))
            );
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("lore")
                            //.requires(Permissions.require(Check.Permission.LORE.getPermission()))
                            .then(CommandManager.argument("text", StringArgumentType.greedyString())
                                    .executes(LoreOperations::addLore))
                            .then(CommandManager.literal("reset")
                                    .executes(LoreOperations::resetLore))
                            .then(CommandManager.literal("help")
                                    .executes(HelpOperations::LoreHelp))
            );
        });

//        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
//            dispatcher.register(
//                    CommandManager.literal("debug")
//                            .executes(Helper::Debug)
//            );
//        });

//        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
//            dispatcher.register(CommandManager.literal("lookup").executes(context -> {
//                context.getSource().sendFeedback(() -> Text.literal("Called /lookup."), false);
//
//                // Get all namespaces
//                context.getSource().getRegistryManager().streamAllRegistryKeys().forEach(System.out::println);
//
//                //var namespaces = registryAccess.getEnabledFeatures();
//                //context.getSource().sendFeedback(() -> Text.literal("Namespaces: " + namespaces), false);
//
//                var registryMan = context.getSource().getRegistryManager();
//                var regs = registryMan.getOrThrow(RegistryKeys.ITEM_SUB_PREDICATE_TYPE);
//
//                var regL = RegistryKey.ofRegistry(Identifier.of("minecraft", "item_predicate_type"));
//
//                for (var item : regs) {
//                    context.getSource().sendFeedback(() -> Text.literal(String.valueOf(item.toString())), false);
//                }
//
//                return 1;
//            }));
//        });
    }
}
