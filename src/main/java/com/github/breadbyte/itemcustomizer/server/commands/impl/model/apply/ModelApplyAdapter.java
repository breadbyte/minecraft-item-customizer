package com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.ModelApplyParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelApplyAdapter implements Adapter<ModelApplyParams> {

    @Override
    public Result<ModelApplyParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.TryReturnValidPlayer(ctx);
        if (player.isErr()) return Result.err(player.unwrapErr());

        var namespace = String.valueOf(ctx.getArgument(com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelApplyCommand.NAMESPACE_ARGUMENT, String.class));
        var category = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_CATEGORY_ARGUMENT, String.class));
        var item = PreOperations.TryGetValidPlayerCurrentHand(ctx.getSource().getPlayer()).unwrap();

        String name;
        NamespaceCategory ns;

        try {
            name = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_NAME_ARGUMENT, String.class));
        } catch (Exception ignored) {
            return Result.err(new Reason.InternalError(ignored.getMessage()));
        }


        if (!category.contains("/")) {
            ns = new NamespaceCategory(namespace, category, name);
        } else {
            // Get the last item of the path, this is the item name, the rest is the category.
            // For example, if we have a path of "old/sword/model", the name is "model" and the category is "old/sword".
            if (category.endsWith("/")) category = category.substring(0, category.length() - 1);
            name = category.split("/")[category.split("/").length - 1];
            ns = new NamespaceCategory(namespace, category.substring(0, category.lastIndexOf("/")), name);
        }

        CustomModelDefinition m = ModelsIndex.getInstance().get(ns, name);

        // todo: access validation here?
        if (m == null) {
            if (!AccessValidator.IsAdmin(player.unwrap())) {
                return Result.err(new Reason.InternalError("No custom model definition found for model: " + ns));
            }

            // Force anyway if we're admin
            m = new CustomModelDefinition(ns, "");
        }

        return Result.ok(new ModelApplyParams(item, ns, m));
    }
}
