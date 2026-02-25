package com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace.ModelNamespaceParams;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class ModelNamespaceAdapter implements Adapter<ModelNamespaceParams> {
    @Override
    public Result<ModelNamespaceParams> getParams(CommandContext<ServerCommandSource> ctx) {
        var paramUrl = String.valueOf(ctx.getArgument("url", String.class));
        var paramNamespace = String.valueOf(ctx.getArgument("namespace", String.class));

        return Result.ok(new ModelNamespaceParams(paramUrl, paramNamespace, ctx.getSource().getServer(), ctx.getSource()));
    }
}
