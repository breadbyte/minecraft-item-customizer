package com.github.breadbyte.itemcustomizer.server.commands.impl.model.lore;

import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lock.ModelLockParams;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore.IModelLoreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.lore.ModelLoreParams;
import com.github.breadbyte.itemcustomizer.server.commands.registry.builder.LoreCommand;
import com.github.breadbyte.itemcustomizer.server.commands.runner.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
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
        NbtCompound defaultTag = playerItem.getItem().getDefaultStack().getNbt();

        // If the default item has default lore, set it to that
        // Otherwise, I don't think there are many items in-game that have lore by default
        if (defaultTag != null
                && defaultTag.contains("display", NbtElement.COMPOUND_TYPE)
                && defaultTag.getCompound("display").contains("Lore", NbtElement.LIST_TYPE)) {
            // 1. Get or create the main item tag compound
            NbtCompound playerTag = playerItem.getOrCreateNbt();

            // 2. Get or create the "display" compound sub-tag
            NbtCompound display;
            if (playerTag.contains("display", NbtElement.COMPOUND_TYPE)) {
                display = playerTag.getCompound("display");
            } else {
                display = new NbtCompound();
                playerTag.put("display", display);
            }

            // 3. Copy the default "Lore" list into the item
            NbtList defaultLore = defaultTag.getCompound("display").getList("Lore", NbtElement.STRING_TYPE);
            display.put("Lore", defaultLore.copy());
        } else {
            // Remove the lore (and the "display" tag if it becomes empty)
            NbtCompound playerTag = playerItem.getNbt();
            if (playerTag != null && playerTag.contains("display", NbtElement.COMPOUND_TYPE)) {
                NbtCompound display = playerTag.getCompound("display");
                display.remove("Lore");
                if (display.isEmpty())
                    playerTag.remove("display");
            }
        }

        return Result.ok();
    }

    private static Result<String> addLoreSingleLine(ItemStack item, String input) {
        // 1. Get or create the main item tag compound
        NbtCompound tag = item.getOrCreateNbt();

        // 2. Get or create the "display" compound sub-tag
        NbtCompound display;
        if (tag.contains("display", NbtElement.COMPOUND_TYPE)) {
            display = tag.getCompound("display");
        } else {
            display = new NbtCompound();
            tag.put("display", display);
        }

        // 3. Get or create the "Lore" list
        NbtList loreList;
        if (display.contains("Lore", NbtElement.LIST_TYPE)) {
            loreList = display.getList("Lore", NbtElement.STRING_TYPE);
        } else {
            loreList = new NbtList();
            display.put("Lore", loreList);
        }

        // 4. Serialize your Text object into Minecraft's mandatory 1.20.1 JSON string format
        String jsonString = Text.Serializer.toJson(Text.of(input));

        // 5. Append the line to the list
        loreList.add(NbtString.of(jsonString));

        return Result.ok();
    }

    private static Result<String> applySplitStyledChars(ItemStack item, List<List<StyledChar>> input) {
        // 1. Get or create the main item tag compound
        NbtCompound tag = item.getOrCreateNbt();

        // 2. Get or create the "display" compound sub-tag
        NbtCompound display;
        if (tag.contains("display", NbtElement.COMPOUND_TYPE)) {
            display = tag.getCompound("display");
        } else {
            display = new NbtCompound();
            tag.put("display", display);
        }

        // 3. Get or create the "Lore" list
        NbtList loreList;
        if (display.contains("Lore", NbtElement.LIST_TYPE)) {
            loreList = display.getList("Lore", NbtElement.STRING_TYPE);
        } else {
            loreList = new NbtList();
            display.put("Lore", loreList);
        }

        // 4. Serialize each styled line into Minecraft's mandatory 1.20.1 JSON string format and append
        for (Text line : styledCharsToTextLines(input)) {
            loreList.add(NbtString.of(Text.Serializer.toJson(line)));
        }

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
        String raw = text.getContent() instanceof LiteralTextContent literal
                ? literal.string()
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
