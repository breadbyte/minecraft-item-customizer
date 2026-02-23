package com.github.breadbyte.itemcustomizer.server.operations.model;

import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class GlintOperations {
    public static Result<Void> toggleGlint(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (getPlayerItem.isErr()) {
            return Result.err(new Reason.NoItem());
        }

        var playerItem = getPlayerItem.unwrap();

        // Refer to table below for logic
        // - HAS OVERRIDE? FLIP THE FLAG, EXIT EARLY
        // - IF ENCHANTED
        //  - SET OVERRIDE TO FALSE (DISABLE GLINT)
        // - IF NOT ENCHANTED
        //  - SET OVERRIDE TO TRUE (ENABLE GLINT)
        var override = playerItem.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        if (override != null) {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, !override);
            return Result.ok();
        }

        if (playerItem.hasEnchantments()) {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            return Result.ok();
        } else {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            return Result.ok();
        }
    }
}
