package com.github.breadbyte.itemcustomizer.server.command;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.operations.ModelOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.EntitySelector;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelCommands {
    // TODO: Cost is static

    public static int applyGlint(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.applyGlint(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            Helper.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());
        return 1;
    }

    public static int removeGlint(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.removeGlint(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            Helper.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }

    public static int applyModel(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var itemType = String.valueOf(ctx.getArgument("item_type", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));
        Integer color = null;
        Boolean changeEquippable = null;
        try { color = ctx.getArgument("color", Integer.class); } catch (Exception ignored) {}
        try { changeEquippable = ctx.getArgument("change_equippable_texture", Boolean.class); } catch (Exception ignored) {}

        var res = ModelOperations.applyModel(player, itemType, itemName, color, changeEquippable);
        if (res.ok()) {
            Helper.SendMessageYes(player, res.details());
            Helper.ApplyCost(player, res.cost());
        } else
            Helper.SendMessageNo(player, res.details());

        return 1;
    }

    public static int resetModel(CommandContext<ServerCommandSource> ctx) {
        var player = Helper.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        var retval = ModelOperations.revertModel(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            Helper.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }

    public static int getPermissionNode(CommandContext<ServerCommandSource> ctx) {
        var itemType = String.valueOf(ctx.getArgument("item_type", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));

        var res = ModelOperations.getPermissionNodeFor(itemType, itemName);
        if (res.ok()) {
            var node = Check.Permission.CUSTOMIZE.chain(res.details());
            ctx.getSource().sendFeedback(() -> Text.literal("Permission node: ").append(Text.literal(node).setStyle(Style.EMPTY.withColor(Formatting.GREEN))), false);
            ctx.getSource().sendFeedback(() -> Text.literal("Click here to copy to clipboard").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, node)).withColor(Formatting.BLUE)), false);
        } else
            ctx.getSource().sendFeedback(() -> Text.of(res.details()), false);

        return 1;
    }

    public static int tintModel(CommandContext<ServerCommandSource> ctx) {
        var index = ctx.getArgument("index", Integer.class);
        var color = ctx.getArgument("color", Integer.class);

        ctx.getSource().sendFeedback(() -> Text.literal("Setting color index " + index + " to " + String.format("0x%06X", (0xFFFFFF & color))), false);
        // TODO: should be able to be executed by console
        if (!ctx.getSource().isExecutedByPlayer())
            return 0;

        var getPlayer = Check.TryReturnValidPlayer(ctx, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isEmpty())
            return 0;

        var player = getPlayer.get();

        var playerItem = player.getMainHandStack();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var dyemap = itemComps.get(DataComponentTypes.CUSTOM_MODEL_DATA);

        // For message output
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // If the data doesn't exist at all, create it
        if (dyemap == null) {
            List<Integer> intList = new java.util.ArrayList<>();
            // populate until specified index
            for (int i = 0; i <= index; i++)
                intList.add(-1);
            intList.set(index, color);

            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(), intList));
            Helper.SendMessageYes(player, "Set color index " + index + " to RGB(" + r + ", " + g + ", " + b + ") for held item");
            return 1;
        }

        // If the data exists but the index is out of bounds, expand it
        if (index >= dyemap.colors().size()) {
            // Deep copy the entire color list
            var newCol = new java.util.ArrayList<>(dyemap.colors());

            if (newCol.size() <= index) {
                // populate until specified index
                for (int i = newCol.size(); i <= index; i++)
                    newCol.add(-1);
            }

            newCol.set(index, color);

            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(), newCol));
            Helper.SendMessageYes(player, "Set color index " + index + " to RGB(" + r + ", " + g + ", " + b + ") for held item");
            return 1;
        }


        // Otherwise, modify existing data
        // Copy existing colors
        var newCol = new java.util.ArrayList<>(dyemap.colors());
        newCol.set(index, color);

        // Write back
        playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(dyemap.floats(), dyemap.flags(), dyemap.strings(), List.copyOf(newCol)));
        Helper.SendMessageYes(player, "Set color index " + index + " to RGB(" + r + ", " + g + ", " + b + ") for held item");
        return 1;
    }

    public static int tintReset(CommandContext<ServerCommandSource> ctx) {
        var getPlayer = Check.TryReturnValidPlayer(ctx, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isEmpty())
            return 0;

        var player = getPlayer.get();

        var playerItem = player.getMainHandStack();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var dyemap = itemComps.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        // Replace color list with empty list, but keep the rest of the data
        playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                dyemap == null ? List.of() : dyemap.floats(),
                dyemap == null ? List.of() : dyemap.flags(),
                dyemap == null ? List.of() : dyemap.strings(),
                List.of()));
        Helper.SendMessageYes(player, "Tints cleared for held item");
        return 1;
    }
}
