package com.github.breadbyte.itemcustomizer.server;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;
import static com.github.breadbyte.itemcustomizer.server.ModelOperations.performChecks;

public class LoreOperations {
    public static int addLore(CommandContext<ServerCommandSource> context) {
        var player = performChecks(context, Check.Permission.LORE.getPermission());
        if (player == null) {
            return 0;
        }
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

        SendMessage(player, String.valueOf(Text.literal("Added ").append(Helper.JsonString2Text(input))), SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);
        return 1;
    }

    public static int resetLore(CommandContext<ServerCommandSource> context) {
        var player = performChecks(context, Check.Permission.LORE.getPermission());
        if (player == null) {
            return 0;
        }

        var playerItem = player.getMainHandStack();

        // Get the default lore for the item
        var defaultItem = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.LORE);

        // Check if the item is currently using the default lore.
        // If it is, do nothing, since we're already using the default lore.
        if (playerItem.getComponents().get(DataComponentTypes.LORE) == defaultItem ||
                playerItem.getComponents().get(DataComponentTypes.LORE) == null) {
            SendMessage(player, "This item is already using the default lore!", SoundEvents.ENTITY_VILLAGER_NO);
            return 0;
        }

        // Else, replace the lore with the default lore
        SendMessage(player, "Lore reset!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        playerItem.set(DataComponentTypes.LORE, defaultItem);
        return 1;
    }
}
