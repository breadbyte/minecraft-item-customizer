package com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.ModelLockParams;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore.IModelLoreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore.ModelLoreParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.LoreCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModelLoreOperations implements IModelLoreOperations {
    private static final int MAX_LINE_LENGTH = 50;

    @Override
    public Result<String> apply(ModelLoreParams params) {
        var input = params.lore();
        var player = params.item();

        if (input.isEmpty())
            return Result.err(Reason.NO_INPUT);

        if (Helper.IsValidJson(input))
            return addLoreSingleLine(player, input);

        var lines = splitLines(input);

        if (lines.size() > 1) {
            for (String line : lines) {
                addLoreSingleLine(player, line);
            }
            return Result.ok();
        }

        return addLoreSingleLine(player, input);
    }

    @Override
    public Result<String> reset(ModelLoreParams params) {
        var playerItem = params.item();

        // Get the default lore for the item
        var defaultItem = playerItem.getItem().getDefaultStack().getComponents();
        var defaultLore = defaultItem.get(DataComponentTypes.LORE);

        // If the default item has default lore, set it to that
        // Otherwise, I don't think there are many items in-game that have lore by default
        if (defaultItem.contains(DataComponentTypes.LORE))
            playerItem.set(DataComponentTypes.LORE, defaultLore);
        else
            playerItem.remove(DataComponentTypes.LORE);

        return Result.ok();
    }

    private static Result<String> addLoreSingleLine(ItemStack item, String input) {
        Text text = Helper.JsonString2Text(input);

        var currentLore = item.get(DataComponentTypes.LORE);

        if (currentLore == null) {
            item.set(DataComponentTypes.LORE, new LoreComponent(new ArrayList<>(List.of(text))));
            return Result.ok();
        }

        var newLines = new ArrayList<>(currentLore.lines());
        newLines.add(text);
        item.set(DataComponentTypes.LORE, new LoreComponent(newLines));

        return Result.ok();
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
}
