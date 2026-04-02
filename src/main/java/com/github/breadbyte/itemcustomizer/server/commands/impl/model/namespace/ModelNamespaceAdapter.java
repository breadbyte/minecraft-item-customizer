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
        URL paramUrl = null;
        String paramNamespace = null;

        try {
            paramNamespace = ctx.getArgument(ModelNamespaceCommand.NAMESPACE_ARGUMENT, String.class);
        } catch (IllegalArgumentException ignored) {
        }

        try {
            paramUrl = URI.create(ctx.getArgument(ModelNamespaceCommand.URL_ARGUMENT, String.class)).toURL();
        } catch (IllegalArgumentException | java.net.MalformedURLException ignored) {
        }

        return Result.ok(new ModelNamespaceParams(paramNamespace, paramUrl, ctx.getSource().getServer(), ctx.getSource()));
    }
}
