package com.github.breadbyte.itemcustomizer.server.commands.impl.model.permission;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.ModelApplyParams;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.permission.ModelPermissionParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelPermissionCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Permission;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.*;

public class ModelPermissionAdapter implements Adapter<ModelPermissionParams> {
    @Override
    public Result<ModelPermissionParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var namespace = String.valueOf(ctx.getArgument(ModelPermissionCommand.NAMESPACE_ARGUMENT, String.class));
        var playerArg = ctx.getArgument(ModelPermissionCommand.PLAYER_ARGUMENT, EntitySelector.class);
        var cmdSrc = ctx.getSource();

        var playerResult = PreOperations.TryReturnValidPlayer(ctx);
        if (playerResult.isErr()) return Result.err(playerResult.unwrapErr());
        var player = playerResult.unwrap();

        List<String> nodes = new ArrayList<>();
        try {
            namespace = ctx.getArgument(ModelPermissionCommand.NAMESPACE_ARGUMENT, String.class);
            for (int i = 1; i <= MAX_AUTOCOMPLETE_NODES; i++) {
                try {
                    nodes.add(ctx.getArgument(NODE_PREFIX + i, String.class));
                } catch (IllegalArgumentException e) {
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            return Result.err(new Reason.InternalError("Missing arguments"));
        }
        if (nodes.isEmpty()) {
            return Result.err(new Reason.InternalError("Missing item path"));
        }

        String fullPath = String.join("/", nodes);
        ModelPath ns = ModelPath.of(String.format("%s:%s", namespace, fullPath));

        Result<CustomModelDefinition> mResult = ModelsIndex.getInstance().getExact(ns);

        if (mResult.isErr()) {
            if (!AccessValidator.IsAdmin(player)) {
                return Result.err(new Reason.InternalError("No custom model definition found for model: " + namespace + ":" + fullPath));
            }

            return Result.err(new Reason.InternalError(mResult.getMessage()));
        }

        // Get the actual model path, which may be different
        ns = mResult.unwrap().getModelPath();

        return Result.ok(new ModelPermissionParams(mResult.unwrap().getModelPath(), cmdSrc, playerArg));
    }
}
