package com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model;

import com.github.breadbyte.itemcustomizer.server.commands.impl.model.wear.ModelWearRunner;
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

public class ModelWearCommand implements BaseCommand {

    private static ModelWearRunner RUNNER;
    public ModelWearCommand(ModelWearRunner runner) {
        RUNNER = runner;
    }

    public static final String SLOT_ARGUMENT = "slot";

    @Override
    public void register(Permission permission, String subCommandName, CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> root) {
        var _root = InternalHelper.RequirePermissionFor(root, permission);

        var WearNode = literal("wear");
        var SlotNode = CommandManager.argument(SLOT_ARGUMENT, StringArgumentType.word()).suggests((ctx, builder) -> {
            builder.suggest("head", new LiteralMessage("Head"));
            builder.suggest("chest", new LiteralMessage("Chestplate"));
            builder.suggest("legs", new LiteralMessage("Leggings"));
            builder.suggest("feet", new LiteralMessage("Boots"));
            return builder.buildFuture();
        });

        dispatcher.register(_root
                .then(WearNode
                .then(SlotNode
                .executes(RUNNER::toggleWearable))));

        dispatcher.register(_root
                .then(WearNode
                .executes(RUNNER::toggleWearable)));
    }
}
