package com.github.breadbyte.itemcustomizer.server.commands.impl.model.namespace;

import com.github.breadbyte.itemcustomizer.server.commands.defs.Adapter;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.namespace.ModelNamespaceParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.model.ModelNamespaceCommand;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import java.net.URI;
import java.net.URL;

public class ModelNamespaceAdapter implements Adapter<ModelNamespaceParams> {
    @Override
    public Result<ModelNamespaceParams> getParams(CommandContext<ServerCommandSource> ctx) {
        URL paramUrl;
        String paramNamespace;

        try {
            paramUrl = URI.create(String.valueOf(ctx.getArgument(ModelNamespaceCommand.URL_ARGUMENT, String.class))).toURL();
            paramNamespace = String.valueOf(ctx.getArgument(ModelNamespaceCommand.NAMESPACE_ARGUMENT, String.class));
        } catch (Exception e) {
            // Clear doesn't need these params, so we can just return an empty params object
            return Result.ok();
        }
        return Result.ok(new ModelNamespaceParams(paramNamespace, paramUrl, ctx.getSource().getServer(), ctx.getSource()));
    }
}
