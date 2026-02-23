package com.github.breadbyte.itemcustomizer.server.operations.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelDyeCommand;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DyeOperations {

    public static Result<Void> DyeModel(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var playerItem = PreOperations.TryGetValidPlayerCurrentHand(player).unwrap();
        var colorClass = ctx.getArgument(ModelDyeCommand.COLOR_ARGUMENT, Integer.class);

        // Get the default dyed color for the item
        var defaultDyedColor = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);
        playerItem.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color.
        playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(colorClass));

        return Result.ok();
    }

    public static Result<Void> ResetDye(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var playerItem = PreOperations.TryGetValidPlayerCurrentHand(player).unwrap();
        playerItem.remove(DataComponentTypes.DYED_COLOR);

        // Get the default dyed color for the item
        var defaultDyedColor = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);

        // Remove the current dyed color component
        playerItem.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color
        playerItem.set(DataComponentTypes.DYED_COLOR, defaultDyedColor);

        return Result.ok();
    }
}