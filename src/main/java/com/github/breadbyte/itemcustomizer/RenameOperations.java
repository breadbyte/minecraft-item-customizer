package com.github.breadbyte.itemcustomizer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.EntityNbtDataSource;
import net.minecraft.text.NbtDataSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Optional;

import static com.github.breadbyte.itemcustomizer.Helper.*;

public class RenameOperations {
    public static int renameItem(CommandContext<ServerCommandSource> context) {
        var player = performChecks(context, Helper.Permission.RENAME.getPermission());
        if (player == null) {
            return 0;
        }
        var playerItem = player.getMainHandStack();

        // Convert NBT text to string and apply the name to the item
        var input = String.valueOf(context.getArgument("name", String.class));

        playerItem.set(DataComponentTypes.ITEM_NAME, JsonString2Text(input));

        Helper.SendMessage(player,Text.literal("Renamed to ").append(Helper.JsonString2Text(input)), SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);

        // Apply the name to the item
        //playerItem.set(DataComponentTypes.ITEM_NAME, Text.of(String.valueOf(context.getArgument("name", String.class))));
        return 1;
    }

    public static int resetName(CommandContext<ServerCommandSource> context) {
        var player = performChecks(context, Helper.Permission.RENAME.getPermission());
        if (player == null) {
            return 0;
        }

        // Very straightforward, just remove the component
        var playerItem = player.getMainHandStack();

        // Get the default item name for the item to compare
        var defaultItem = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.ITEM_NAME);

        // Check if the item is currently using the default name.
        // If it is, do nothing, since we're already using the default name.
        if (playerItem.getComponents().get(DataComponentTypes.ITEM_NAME) == defaultItem ||
                playerItem.getComponents().get(DataComponentTypes.ITEM_NAME) == null) {
            Helper.SendMessage(player, "This item is already using the default name!", null);
            return 0;
        }


        // Else, replace the name with the default name
        Helper.SendMessage(player, "Item name reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        playerItem.set(DataComponentTypes.ITEM_NAME, defaultItem);

        return 1;
    }
}
