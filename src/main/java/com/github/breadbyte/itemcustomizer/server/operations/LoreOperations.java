package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class LoreOperations {
    public static int addLore(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();
        var input = String.valueOf(context.getArgument("text", String.class));

        // Get the currently applied lore in the item
        var currentLore = playerItem.get(DataComponentTypes.LORE);

        // If there is no lore, create a new one
        if (currentLore == null) {
            var customLore = new LoreComponent(new ArrayList<Text>() {{
                add(Text.of(Helper.JsonString2Text(input)));
            }});

            playerItem.set(DataComponentTypes.LORE, customLore);
            return 1;
        }

        // Create a hard copy of the current lore
        var newLine = new ArrayList<Text>(currentLore.lines());

        // Append the new lore to the existing lore
        newLine.add(Text.of(Helper.JsonString2Text(input)));

        LoreComponent newLore = new LoreComponent(newLine);
        playerItem.set(DataComponentTypes.LORE, newLore);

        Helper.SendMessage(player, String.valueOf(Text.literal("Added ").append(Helper.JsonString2Text(input))), SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);
        return 1;
    }

    public static int resetLore(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Get the default lore for the item
        var defaultItem = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.LORE);

        // Check if the item is currently using the default lore.
        // If it is, do nothing, since we're already using the default lore.
        if (playerItem.getComponents().get(DataComponentTypes.LORE) == defaultItem ||
                playerItem.getComponents().get(DataComponentTypes.LORE) == null) {
            Helper.SendMessage(player, "This item is already using the default lore!", SoundEvents.ENTITY_VILLAGER_NO);
            return 0;
        }

        // Else, replace the lore with the default lore
        Helper.SendMessage(player, "Lore reset!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        playerItem.set(DataComponentTypes.LORE, defaultItem);
        return 1;
    }
}
