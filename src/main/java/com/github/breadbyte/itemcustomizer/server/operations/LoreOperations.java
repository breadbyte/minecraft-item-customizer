package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.data.OperationResult;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoreOperations {
    private static final int MAX_LINE_LENGTH = 50;

    public static OperationResult addLore(ServerPlayerEntity player, String input) {
        if (input.isEmpty())
            return OperationResult.fail("Empty input!");

        if (Helper.IsValidJson(input))
            return addLoreSingleLine(player, input);

        var lines = splitLines(input);

        if (lines.size() > 1) {
            for (String line : lines) {
                addLoreSingleLine(player, line);
            }
            return OperationResult.ok("Lore added", lines.size());
        }

        return addLoreSingleLine(player, input);
    }

    private static OperationResult addLoreSingleLine(ServerPlayerEntity player, String input) {
        var playerItem = player.getMainHandStack();

        Text text = Helper.JsonString2Text(input);

        var currentLore = playerItem.get(DataComponentTypes.LORE);

        if (currentLore == null) {
            playerItem.set(DataComponentTypes.LORE, new LoreComponent(new ArrayList<>(List.of(text))));
            return OperationResult.ok("Lore added", 1);
        }

        var newLines = new ArrayList<>(currentLore.lines());
        newLines.add(text);
        playerItem.set(DataComponentTypes.LORE, new LoreComponent(newLines));

        return OperationResult.ok("Lore added", 1);
    }

    private static ArrayList<String> splitLines(String input) {
        var lines = new ArrayList<String>();
        BreakIterator iterator = BreakIterator.getLineInstance(Locale.ROOT);
        iterator.setText(input);
        int start = iterator.first();
        StringBuilder current = new StringBuilder();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String segment = input.substring(start, end);
            boolean isNewline = segment.contains("\n");

            if (isNewline || current.length() + segment.length() > MAX_LINE_LENGTH) {
                if (!current.isEmpty()) {
                    lines.add(current.toString().stripTrailing());
                    current.setLength(0);
                }
                if (!isNewline && !segment.isEmpty())
                    current.append(segment);
            } else {
                current.append(segment);
            }
        }
        if (!current.isEmpty())
            lines.add(current.toString().stripTrailing());
        return lines;
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
