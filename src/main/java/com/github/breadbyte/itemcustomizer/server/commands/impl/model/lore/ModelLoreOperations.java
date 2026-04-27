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
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
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

        if (Helper.IsValidJson(input)) {
            List<StyledChar> flat = flattenText(Helper.JsonString2Text(input));
            applySplitStyledChars(player, splitStyledChars(flat));
            return Result.ok();
        }

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

    private static Result<String> applySplitStyledChars(ItemStack item, List<List<StyledChar>> input) {
        var currentLore = item.get(DataComponentTypes.LORE);

        if (currentLore == null) {
            item.set(DataComponentTypes.LORE, new LoreComponent(styledCharsToTextLines(input)));
            return Result.ok();
        }

        var newLines = new ArrayList<>(currentLore.lines());
        newLines.addAll(styledCharsToTextLines(input));
        item.set(DataComponentTypes.LORE, new LoreComponent(newLines));

        return Result.ok();
    }

    private static List<Text> styledCharsToTextLines(List<List<StyledChar>> lines) {
        var result = new ArrayList<Text>();

        for (List<StyledChar> line : lines) {
            result.add(lineToText(line));
        }

        return result;
    }

    private static Text lineToText(List<StyledChar> line) {
        if (line.isEmpty())
            return Text.empty();

        MutableText root = null;

        int i = 0;
        while (i < line.size()) {
            StyledChar first = line.get(i);
            Style style = first.style();

            // Accumulate consecutive characters sharing the same style
            StringBuilder sb = new StringBuilder();
            while (i < line.size() && line.get(i).style().equals(style)) {
                sb.append(line.get(i).value());
                i++;
            }

            MutableText segment = Text.literal(sb.toString()).setStyle(style);

            if (root == null) {
                root = segment;
            } else {
                root.append(segment);
            }
        }

        return root; // root is never null here — line is non-empty
    }

    private static List<StyledChar> flattenText(Text input) {
        var flat = new ArrayList<StyledChar>();
        collectChars(input, flat);
        return flat;
    }

    private static void collectChars(Text text, List<StyledChar> out) {
        Style style = text.getStyle();
        String raw = text.getContent() instanceof PlainTextContent plain
                ? plain.string()
                : text.getString(); // fallback for TranslatableTextContent etc.

        for (char c : raw.toCharArray()) {
            out.add(new StyledChar(c, style));
        }

        for (Text sibling : text.getSiblings()) {
            collectChars(sibling, out);
        }
    }

    private static List<List<StyledChar>> wrapParagraph(List<StyledChar> chars) {
        var lines = new ArrayList<List<StyledChar>>();
        if (chars.isEmpty()) return lines;

        // Reconstruct plain string for BreakIterator — index-aligned with chars list
        StringBuilder sb = new StringBuilder(chars.size());
        for (StyledChar c : chars) sb.append(c.value());
        String text = sb.toString();

        BreakIterator iterator = BreakIterator.getLineInstance(Locale.ROOT);
        iterator.setText(text);

        int start = iterator.first();
        var current = new ArrayList<StyledChar>();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            List<StyledChar> segment = chars.subList(start, end);

            if (current.size() + segment.size() > MAX_LINE_LENGTH) {
                // Soft wrap: flush current line, carry segment to next
                if (!current.isEmpty()) {
                    lines.add(trimTrailing(current));
                    current = new ArrayList<>();
                }
                // Segment itself may exceed MAX_LINE_LENGTH (e.g. "supercalifragilistic")
                // Force-split it by character
                for (StyledChar c : segment) {
                    current.add(c);
                    if (current.size() >= MAX_LINE_LENGTH) {
                        lines.add(trimTrailing(current));
                        current = new ArrayList<>();
                    }
                }
            } else {
                current.addAll(segment);
            }
        }

        if (!current.isEmpty())
            lines.add(trimTrailing(current));

        return lines;
    }

    private static List<List<StyledChar>> splitStyledChars(List<StyledChar> chars) {
        var lines = new ArrayList<List<StyledChar>>();
        var paragraph = new ArrayList<StyledChar>();

        int i = 0;
        while (i < chars.size()) {
            StyledChar c = chars.get(i);

            // Detect literal two-char \n from user input
            if (c.value() == '\\' && i + 1 < chars.size() && chars.get(i + 1).value() == 'n') {
                lines.addAll(wrapParagraph(paragraph));
                lines.add(new ArrayList<>());  // blank line at the hard break
                paragraph = new ArrayList<>();
                i += 2; // consume both \ and n
            } else {
                paragraph.add(c);
                i++;
            }
        }

        if (!paragraph.isEmpty())
            lines.addAll(wrapParagraph(paragraph));

        return lines;
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

    private static List<StyledChar> trimTrailing(List<StyledChar> line) {
        int end = line.size();
        while (end > 0 && Character.isWhitespace(line.get(end - 1).value()))
            end--;
        return new ArrayList<>(line.subList(0, end));
    }
}
