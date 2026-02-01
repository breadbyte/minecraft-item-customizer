package com.github.breadbyte.itemcustomizer.server;

import com.github.breadbyte.itemcustomizer.server.command.GrantCommands;
import com.github.breadbyte.itemcustomizer.server.command.ModelCommands;
import com.github.breadbyte.itemcustomizer.server.command.LoreCommands;
import com.github.breadbyte.itemcustomizer.server.command.RenameCommands;
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
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("model")
                            .requires(Permissions.require(Check.Permission.CUSTOMIZE.getPermission())
                                    .or(scs -> Objects.requireNonNull(scs.getPlayer()).isCreative())
                                    .or(scs -> {
                                        // Check if executed by player, if so, check if they are an operator
                                        if (scs.isExecutedByPlayer())
                                            return scs.getServer().getPlayerManager().isOperator(Objects.requireNonNull(scs.getPlayer()).getPlayerConfigEntry());
                                        // Make this check true otherwise for everything else
                                        else return true;
                                    })
                            )
                            .then(CommandManager.literal("apply")
                                    .then(CommandManager.argument("item_type", StringArgumentType.word())
                                            .suggests(ModelCategorySuggestionProvider.INSTANCE)
                                            .then(CommandManager.argument("item_name", StringArgumentType.string())
                                                    .suggests(ModelSuggestionProvider.INSTANCE)
                                                    .executes(ModelCommands::applyModel)
                                                    .then(CommandManager.argument("change_equippable_texture", BoolArgumentType.bool()).executes(ModelCommands::applyModel))
                                                .then(CommandManager.argument("color", IntegerArgumentType.integer()).executes(ModelCommands::applyModel)
                                                    .then(CommandManager.argument("change_equippable_texture", BoolArgumentType.bool()).executes(ModelCommands::applyModel)))
                                    ))
                            )
                            .then(CommandManager.literal("tint")
                                .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                                    .then(CommandManager.argument("color", IntegerArgumentType.integer(Integer.MIN_VALUE, Integer.MAX_VALUE))
                                        .executes(ModelCommands::tintModel)
                                    ))
                                .then(CommandManager.literal("reset").executes(ModelCommands::tintReset))
                            )
                            .then(CommandManager.literal("glint")
                                    .then(CommandManager.literal("on")
                                            .executes(ModelCommands::applyGlint)
                                    )
                                    .then(CommandManager.literal("off")
                                            .executes(ModelCommands::removeGlint)
                                    )
                            )
                            .then(CommandManager.literal("wear")
                                    .executes(ModelCommands::toggleWearable)
                            )
                            .then(CommandManager.literal("reset")
                                    .executes(ModelCommands::resetModel)
                            )
                            .then(CommandManager.literal("permission")
                                .then(CommandManager.literal("get")
                                        .then(CommandManager.argument("item_type", StringArgumentType.word())
                                                .suggests(ModelCategorySuggestionProvider.INSTANCE)
                                                .then(CommandManager.argument("item_name", StringArgumentType.string())
                                                        .suggests(ModelSuggestionProvider.INSTANCE)
                                                        .executes(ModelCommands::getPermissionNode)
                                                )
                                        )
                                )
                            )
                            .then(CommandManager.literal("namespaces")
                                    .requires(Permissions.require(Check.Permission.ADMIN.getPermission())
                                    .or(scs -> {
                                         // Check if executed by player, if so, check if they are an operator
                                         if (scs.isExecutedByPlayer())
                                            return scs.getServer().getPlayerManager().isOperator(Objects.requireNonNull(scs.getPlayer()).getPlayerConfigEntry());
                                         // Make this check true otherwise for everything else
                                        else return true;
                                    }))
                                .then(CommandManager.literal("register")
                                        .then(CommandManager.argument("namespace", StringArgumentType.word())
                                                .then(CommandManager.argument("csv_url", StringArgumentType.greedyString())
                                                        .executes(SuggestionOperations::registerSuggestions))
                                        )
                                )
                                .then(CommandManager.literal("clear")
                                        .executes(SuggestionOperations::clearSuggestions)
                                )
                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("namespace", StringArgumentType.word())
                                                .suggests(NamespaceSuggestionProvider.INSTANCE)
                                                .executes(SuggestionOperations::removeNamespace))
                                )
                            )
                            .then(CommandManager.literal("permission")
                                    .requires(Permissions.require(Check.Permission.GRANT.getPermission()))
                                    .then(CommandManager.literal("grant")
                                            .then(CommandManager.argument("item_type", StringArgumentType.word())
                                                    .suggests(ModelCategorySuggestionProvider.INSTANCE)
                                            .then(CommandManager.argument("item_name", StringArgumentType.string())
                                                    .suggests(ModelSuggestionProvider.INSTANCE)
                                                    .then(CommandManager.argument("player", EntityArgumentType.player())
                                                            .executes(GrantCommands::grantModelPerm)))))
                                    .then(CommandManager.literal("revoke")
                                            .then(CommandManager.argument("item_type", StringArgumentType.word())
                                                    .suggests(ModelCategorySuggestionProvider.INSTANCE)
                                            .then(CommandManager.argument("item_name", StringArgumentType.string())
                                                    .suggests(ModelSuggestionProvider.INSTANCE)
                                                    .then(CommandManager.argument("player", EntityArgumentType.player())
                                                            .executes(GrantCommands::revokeModelPerm)))))
            ));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("rename")
                            .requires(Permissions.require(Check.Permission.RENAME.getPermission())
                                    .or(scs -> scs.getPlayer().isCreative())
                                    .or(scs -> {
                                        // Check if executed by player, if so, check if they are an operator
                                        if (scs.isExecutedByPlayer())
                                            return scs.getServer().getPlayerManager().isOperator(Objects.requireNonNull(scs.getPlayer()).getPlayerConfigEntry());
                                            // Make this check true otherwise for everything else
                                        else return true;
                                    }))
                            .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                    .executes(RenameCommands::renameItem))
                            .then(CommandManager.literal("reset")
                                    .executes(RenameCommands::resetName))
                            .then(CommandManager.literal("help")
                                    .executes(HelpOperations::RenameHelp))
            );
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("lore")
                            .requires(Permissions.require(Check.Permission.LORE.getPermission())
                                    .or(scs -> scs.getPlayer().isCreative())
                                    .or(scs -> {
                                       // Check if executed by player, if so, check if they are an operator
                                       if (scs.isExecutedByPlayer())
                                          return scs.getServer().getPlayerManager().isOperator(Objects.requireNonNull(scs.getPlayer()).getPlayerConfigEntry());
                                       // Make this check true otherwise for everything else
                                       else return true;
                                       })
                                    )
                            .then(CommandManager.argument("text", StringArgumentType.greedyString())
                                    .executes(LoreCommands::addLore))
                            .then(CommandManager.literal("reset")
                                    .executes(LoreCommands::resetLore))
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
