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

public class ModelApplyAdapter implements Adapter<ModelApplyParams> {

    @Override
    public Result<ModelApplyParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var playerResult = PreOperations.TryReturnValidPlayer(ctx);
        if (playerResult.isErr()) return Result.err(playerResult.unwrapErr());
        var player = playerResult.unwrap();

        String namespace;
        String path;
        try {
            namespace = String.valueOf(ctx.getArgument(ModelApplyCommand.NAMESPACE_ARGUMENT, String.class));
            path = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_PATH_ARGUMENT, String.class));
        } catch (IllegalArgumentException e) {
            return Result.err(new Reason.InternalError("Missing arguments"));
        }

        var item = PreOperations.TryGetValidPlayerCurrentHand(player).unwrap();

        ModelPath ns = ModelPath.fromNamespaceAndPath(namespace, path);
        CustomModelDefinition m = ModelsIndex.getInstance().get(ns, ns.itemName());

        if (m == null) {
            if (!AccessValidator.IsAdmin(player)) {
                return Result.err(new Reason.InternalError("No custom model definition found for model: " + ns));
            }

            // Force anyway if we're admin
            m = new CustomModelDefinition(ns, "");
        }

        return Result.ok(new ModelApplyParams(item, ns, m));
    }
}
