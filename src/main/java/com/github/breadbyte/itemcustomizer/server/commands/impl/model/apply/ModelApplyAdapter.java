package com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.ModelApplyParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelPath;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

import static com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.*;

public class ModelApplyAdapter implements Adapter<ModelApplyParams> {

    @Override
    public Result<ModelApplyParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var playerResult = PreOperations.TryReturnValidPlayer(ctx);
        if (playerResult.isErr()) return Result.err(playerResult.unwrapErr());
        var player = playerResult.unwrap();

        String namespace;
        List<String> nodes = new ArrayList<>();
        try {
            namespace = ctx.getArgument(NAMESPACE_ARGUMENT, String.class);
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

        var itemResult = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (itemResult.isErr()) return Result.err(itemResult.unwrapErr());
        var item = itemResult.unwrap();

        String fullPath = String.join("/", nodes);
        ModelPath ns = ModelPath.of(fullPath);

        CustomModelDefinition m = ModelsIndex.getInstance().get(ns).stream().findFirst().orElse(null);

        if (m == null) {
            if (!AccessValidator.IsAdmin(player)) {
                return Result.err(new Reason.InternalError("No custom model definition found for model: " + namespace + ":" + fullPath));
            }

            // Force anyway if we're admin
            return Result.ok(new ModelApplyParams(item, ns, null));
        }

        // Get the actual model path, which may be different
        ns = m.getModelPath();

        return Result.ok(new ModelApplyParams(item, ns, m));
    }
}
