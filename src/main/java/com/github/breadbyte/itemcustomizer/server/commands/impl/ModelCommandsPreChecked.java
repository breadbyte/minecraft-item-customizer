package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.commands.PreOperations;
import com.github.breadbyte.itemcustomizer.server.operations.ModelOperations;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelCommandsPreChecked {
    // TODO: Cost is static
    public static int lockModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        var retval = ModelOperations.lockModel(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            PreOperations.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }

    public static int toggleGlint(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        var retval = ModelOperations.toggleGlint(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            PreOperations.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());
        return 1;
    }

    public static int applyModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        var namespace = String.valueOf(ctx.getArgument("namespace", String.class));
        var itemType = String.valueOf(ctx.getArgument("item_category", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));
        Integer color = null;
        Boolean changeEquippable = null;
        try { color = ctx.getArgument("color", Integer.class); } catch (Exception ignored) {}
        try { changeEquippable = ctx.getArgument("change_equippable_texture", Boolean.class); } catch (Exception ignored) {}

        var res = ModelOperations.applyModel(player, namespace, itemType, itemName, color, changeEquippable);
        if (res.ok()) {
            Helper.SendMessageYes(player, res.details());
            PreOperations.ApplyCost(player, res.cost());
        } else
            Helper.SendMessageNo(player, res.details());

        return 1;
    }

    public static int resetModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        var retval = ModelOperations.revertModel(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            PreOperations.ApplyCost(player, retval.cost());
        } else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }

    public static int getPermissionNode(CommandContext<ServerCommandSource> ctx) {
        var itemType = String.valueOf(ctx.getArgument("item_category", String.class));
        var itemName = String.valueOf(ctx.getArgument("item_name", String.class));

        var res = ModelOperations.getPermissionNodeFor(itemType, itemName);
        if (res.ok()) {
            var node = Check.Permission.CUSTOMIZE.chain(res.details());
            ctx.getSource().sendFeedback(() -> Text.literal("Permission node: ").append(Text.literal(node).setStyle(Style.EMPTY.withColor(Formatting.GREEN))), false);
            ctx.getSource().sendFeedback(() -> Text.literal("Click here to copy to clipboard").setStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(node)).withColor(Formatting.BLUE)), false);
        } else
            ctx.getSource().sendFeedback(() -> Text.of(res.details()), false);

        return 1;
    }

    public static int tintModel(CommandContext<ServerCommandSource> ctx) {
        var index = ctx.getArgument("tint_index", Integer.class);
        var color = ctx.getArgument("tint_color", Integer.class);
        // color is hex color

        ctx.getSource().sendFeedback(() -> Text.literal("Setting color index " + index + " to " + String.format("#%06X", (0xFFFFFF & color))), false);
        // TODO: should be able to be executed by console
        if (!ctx.getSource().isExecutedByPlayer())
            return 0;

        var getPlayer = PreOperations.TryReturnValidPlayer(ctx, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isEmpty())
            return 0;

        var player = getPlayer.get();

        var playerItem = player.getMainHandStack();

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var dyemap = itemComps.get(DataComponentTypes.CUSTOM_MODEL_DATA);

        // If the data doesn't exist at all, create it
        if (dyemap == null) {
            List<Integer> intList = new java.util.ArrayList<>();
            // populate until specified index
            for (int i = 0; i <= index; i++)
                intList.add(-1);
            intList.set(index, color);

            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(), intList));
            Helper.SendMessageYes(player, "Set color index " + index + " to #" + String.format("%06X", (0xFFFFFF & color)) + " for held item");
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
            Helper.SendMessageYes(player, "Set color index " + index + " to #" + String.format("%06X", (0xFFFFFF & color)) + " for held item");
            return 1;
        }


        // Otherwise, modify existing data
        // Copy existing colors
        var newCol = new java.util.ArrayList<>(dyemap.colors());
        newCol.set(index, color);

        // Write back
        playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(dyemap.floats(), dyemap.flags(), dyemap.strings(), List.copyOf(newCol)));
        Helper.SendMessageYes(player, "Set color index " + index + " to #" + String.format("%06X", (0xFFFFFF & color)) + " for held item");
        return 1;
    }

    public static int tintReset(CommandContext<ServerCommandSource> ctx) {
        var getPlayer = PreOperations.TryReturnValidPlayer(ctx, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isEmpty())
            return 0;

        var player = getPlayer.get();

        var playerItem = player.getMainHandStack();

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

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

    public static int toggleWearable(CommandContext<ServerCommandSource> ctx) {
        if (!ctx.getSource().isExecutedByPlayer())
            return 0;

        var getPlayer = PreOperations.TryReturnValidPlayer(ctx, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isEmpty())
            return 0;

        var player = getPlayer.get();

        var playerItem = player.getMainHandStack();

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var equippableComponent = itemComps.get(DataComponentTypes.EQUIPPABLE);

        // If the component doesn't exist, add it, otherwise, remove it
        if (equippableComponent == null) {
            playerItem.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD).build());
            Helper.SendMessageYes(player, "You can now equip this item as a wearable");
        } else {
            playerItem.set(DataComponentTypes.EQUIPPABLE, playerItem.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE));
            Helper.SendMessageYes(player, "Item equippable reset");
        }

        return 1;
    }

    public static int dyeModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        var colorClass = ctx.getArgument("dye_color", Integer.class);
        var retval = ModelOperations.applyDyedColor(player, colorClass);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            PreOperations.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());
        return 1;
    }

    public static int dyeReset(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        var retval = ModelOperations.revertDyedColor(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            PreOperations.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());
        return 1;
    }

    public static int unlockModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateState(ctx, 1);
        if (player == null)
            return 0;

        if (!PreOperations.IsModelOwner(player)) {
            Helper.SendMessageNo(player, "Model is locked!");
            return 0;
        }

        var retval = ModelOperations.lockModel(player);

        if (retval.ok()) {
            Helper.SendMessageYes(player, retval.details());
            PreOperations.ApplyCost(player, retval.cost());
        }
        else
            Helper.SendMessageNo(player, retval.details());

        return 1;
    }
}
