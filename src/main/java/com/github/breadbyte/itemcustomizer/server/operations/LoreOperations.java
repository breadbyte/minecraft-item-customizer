package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.OperationResult;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class LoreOperations {
    public static OperationResult addLore(ServerPlayerEntity player, String input) {
        var playerItem = player.getMainHandStack();

        // Get the currently applied lore in the item
        var currentLore = playerItem.get(DataComponentTypes.LORE);

        // If there is no lore, create a new one
        if (currentLore == null) {
            var customLore = new LoreComponent(new ArrayList<Text>() {{
                add(Text.of(Helper.JsonString2Text(input)));
            }});

            playerItem.set(DataComponentTypes.LORE, customLore);
            return OperationResult.ok("Lore added", 1);
        }

        // Create a hard copy of the current lore
        var newLine = new ArrayList<Text>(currentLore.lines());

        // Append the new lore to the existing lore
        newLine.add(Text.of(Helper.JsonString2Text(input)));

        LoreComponent newLore = new LoreComponent(newLine);
        playerItem.set(DataComponentTypes.LORE, newLore);

        return OperationResult.ok(String.valueOf(Text.literal("Added ").append(Helper.JsonString2Text(input))), 1);
    }

    public static OperationResult resetLore(ServerPlayerEntity player) {
        var playerItem = player.getMainHandStack();

        // Get the default lore for the item
        var defaultItem = playerItem.getItem().getDefaultStack().getComponents();
        var defaultLore = defaultItem.get(DataComponentTypes.LORE);

        // If the default item has default lore, set it to that
        // Otherwise, I don't think there are many items in-game that have lore by default
        if (defaultItem.contains(DataComponentTypes.LORE))
            playerItem.set(DataComponentTypes.LORE, defaultLore);
        else
            playerItem.remove(DataComponentTypes.LORE);

        return OperationResult.ok("Lore reset!");
    }
}
