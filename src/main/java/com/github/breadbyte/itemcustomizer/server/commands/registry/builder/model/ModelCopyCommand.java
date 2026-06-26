package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.copy.ModelCopyRunner;
import com.github.breadbyte.itemcustomizer.server.commands.registry.BaseCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registry.InternalHelper;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ModelCopyCommand implements BaseCommand {

    private static ModelCopyRunner RUNNER;
    public ModelCopyCommand(ModelCopyRunner runner) {
        RUNNER = runner;
    }

    public static final String COPY_WHAT_ARGUMENT = "what";
    public static final String COPY_TO_ARGUMENT = "to";

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        // The 'root' here is already the 'model' command.
        // We need to apply the permission to the 'copy' subcommand.

        var CopyNode = InternalHelper.RequirePermissionFor(literal("copy"), permission);

        var what = CommandManager.argument(COPY_WHAT_ARGUMENT, StringArgumentType.word()).suggests((ctx, builder) -> {
            builder.suggest("model", new LiteralMessage("Model"));
            builder.suggest("name", new LiteralMessage("Name"));
            builder.suggest("lore", new LiteralMessage("Lore"));
            builder.suggest("all", new LiteralMessage("All"));
            return builder.buildFuture();
        });

        var to = CommandManager.argument(COPY_TO_ARGUMENT, StringArgumentType.word()).suggests((ctx, builder) -> {
                    builder.suggest("mainhand", new LiteralMessage("Main Hand"));
                    builder.suggest("hotbar", new LiteralMessage("Hotbar"));
                    builder.suggest("inventory", new LiteralMessage("All items in inventory"));
                    return builder.buildFuture();
                });

        // model copy
        dispatcher.register(root
                .then(CopyNode
                .then(what
                .then(to
                .executes(RUNNER::copy)))));
    }
}