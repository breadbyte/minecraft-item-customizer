package com.github.breadbyte.itemcustomizer.server.commands.impl;

import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelApplyCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelDyeCommand;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelTintCommand;
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

import static com.github.breadbyte.itemcustomizer.server.util.Helper.SendMessage;

public class ModelCommandsPreChecked {
    // TODO: Cost is static
    public static int lockModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var retval = ModelOperations.lockModel(player.unwrap());

        if (retval.ok()) {
            Helper.SendMessage(ctx.getSource(), retval.details());
            PreOperations.TryApplyCost(player.unwrap(), retval.cost());
        }
        else
            Helper.SendError(ctx.getSource(), retval.details());

        return 1;
    }

    public static int toggleGlint(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var retval = ModelOperations.toggleGlint(player.unwrap());

        if (retval.ok()) {
            Helper.SendMessage(ctx.getSource(), retval.details());
            PreOperations.TryApplyCost(player.unwrap(), retval.cost());
        }
        else
            Helper.SendError(ctx.getSource(), retval.details());

        return 1;
    }

    public static int oldFormatApplyModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        // Arguments that exist normally
        var namespace = String.valueOf(ctx.getArgument(ModelApplyCommand.NAMESPACE_ARGUMENT, String.class));
        var fullPath = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_CATEGORY_ARGUMENT, String.class));

        var namespaceCategory = new NamespaceCategory(namespace, fullPath.contains("/") ? fullPath.substring(0, fullPath.lastIndexOf("/")) : fullPath);

        // Arguments that don't necessarily exist
        Integer color = null;
        Boolean changeEquippable = null;
        try { color = ctx.getArgument(ModelApplyCommand.COLOR_ARGUMENT, Integer.class); } catch (Exception ignored) {}
        try { changeEquippable = ctx.getArgument(ModelApplyCommand.EQUIPMENT_TEXTURE_ARGUMENT, Boolean.class); } catch (Exception ignored) {}

        if (fullPath.contains("/")) {
            var split = fullPath.split("/");
            var res = ModelOperations.applyModel(player.unwrap(), namespace, split[0], split[split.length - 1], color, changeEquippable);
            if (res.ok()) {
                Helper.SendMessage(ctx.getSource(), res.details());
                PreOperations.TryApplyCost(player.unwrap(), res.cost());
            } else
                Helper.SendError(ctx.getSource(), res.details());
            return 1;
        }

        return 0;
    }

    // todo: ApplyModel must use NamespaceCategory to simplify and typecheck
    // todo: unify oldFormatApplyModel and applyModel
    public static int applyModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        // Arguments that exist normally
        var namespace = String.valueOf(ctx.getArgument(ModelApplyCommand.NAMESPACE_ARGUMENT, String.class));
        var itemType = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_CATEGORY_ARGUMENT, String.class));

        // Arguments that don't necessarily exist
        Integer color = null;
        Boolean changeEquippable = null;
        try { color = ctx.getArgument(ModelApplyCommand.COLOR_ARGUMENT, Integer.class); } catch (Exception ignored) {}
        try { changeEquippable = ctx.getArgument(ModelApplyCommand.EQUIPMENT_TEXTURE_ARGUMENT, Boolean.class); } catch (Exception ignored) {}

        if (itemType.contains("/")) {
            var split = itemType.split("/");
            var res = ModelOperations.applyModel(player.unwrap(), namespace, split[0], split[split.length - 1], color, changeEquippable);
            if (res.ok()) {
                Helper.SendMessage(ctx.getSource(), res.details());
                PreOperations.TryApplyCost(player.unwrap(), res.cost());
            } else
                Helper.SendError(ctx.getSource(), res.details());
            return 1;
        }


        var itemName = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_NAME_ARGUMENT, String.class));

        var res = ModelOperations.applyModel(player.unwrap(), namespace, itemType, itemName, color, changeEquippable);
        if (res.ok()) {
            Helper.SendMessage(ctx.getSource(), res.details());
            PreOperations.TryApplyCost(player.unwrap(), res.cost());
        } else
            Helper.SendError(ctx.getSource(), res.details());

        return 1;
    }

    public static int resetModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var retval = ModelOperations.revertModel(player.unwrap());

        if (retval.ok()) {
            Helper.SendMessage(ctx.getSource(), retval.details());
            PreOperations.TryApplyCost(player.unwrap(), retval.cost());
        } else
            Helper.SendError(ctx.getSource(), retval.details());

        return 1;
    }

    public static int getPermissionNode(CommandContext<ServerCommandSource> ctx) {
        var itemType = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_CATEGORY_ARGUMENT, String.class));
        var itemName = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_NAME_ARGUMENT, String.class));

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
        var index = ctx.getArgument(ModelTintCommand.TINT_INDEX_ARGUMENT, Integer.class);
        var color = ctx.getArgument(ModelTintCommand.TINT_COLOR_ARGUMENT, Integer.class);
        // color is hex color

        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var playerItemResult = PreOperations.TryGetValidPlayerCurrentHand(player.unwrap());
        if (playerItemResult.isErr()) {
            playerItemResult.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var playerItem = playerItemResult.unwrap();

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
            Helper.SendMessage(ctx.getSource(), "Set color index " + index + " to #" + String.format("%06X", (0xFFFFFF & color)) + " for held item");
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
            Helper.SendMessage(ctx.getSource(), "Set color index " + index + " to #" + String.format("%06X", (0xFFFFFF & color)) + " for held item");
            return 1;
        }


        // Otherwise, modify existing data
        // Copy existing colors
        var newCol = new java.util.ArrayList<>(dyemap.colors());
        newCol.set(index, color);

        // Write back
        playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(dyemap.floats(), dyemap.flags(), dyemap.strings(), List.copyOf(newCol)));
        Helper.SendMessage(ctx.getSource(), "Set color index " + index + " to #" + String.format("%06X", (0xFFFFFF & color)) + " for held item");
        return 1;
    }

    public static int tintReset(CommandContext<ServerCommandSource> ctx) {
        var getPlayer = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isErr()) {
            getPlayer.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var player = getPlayer.unwrap();

        var playerItemResult = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (playerItemResult.isErr()) {
            playerItemResult.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var playerItem = playerItemResult.unwrap();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var dyemap = itemComps.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        // Replace color list with empty list, but keep the rest of the data
        playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                dyemap == null ? List.of() : dyemap.floats(),
                dyemap == null ? List.of() : dyemap.flags(),
                dyemap == null ? List.of() : dyemap.strings(),
                List.of()));
        Helper.SendMessage(ctx.getSource(), "Tints cleared for held item");
        return 1;
    }

    public static int toggleWearable(CommandContext<ServerCommandSource> ctx) {
        var getPlayer = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isErr()) {
            getPlayer.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var player = getPlayer.unwrap();

        var playerItemResult = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (playerItemResult.isErr()) {
            playerItemResult.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var playerItem = playerItemResult.unwrap();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        var equippableComponent = itemComps.get(DataComponentTypes.EQUIPPABLE);

        // If the component doesn't exist, add it, otherwise, remove it
        if (equippableComponent == null) {
            playerItem.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD).build());
            Helper.SendMessage(ctx.getSource(), "You can now equip this item as a wearable");
        } else {
            playerItem.set(DataComponentTypes.EQUIPPABLE, playerItem.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE));
            Helper.SendMessage(ctx.getSource(), "Item equippable reset");
        }

        return 1;
    }

    public static int dyeModel(CommandContext<ServerCommandSource> ctx) {
        var getPlayer = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (getPlayer.isErr()) {
            getPlayer.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var player = getPlayer.unwrap();

        var colorClass = ctx.getArgument(ModelDyeCommand.COLOR_ARGUMENT, Integer.class);
        var retval = ModelOperations.applyDyedColor(player, colorClass);

        if (retval.ok()) {
            Helper.SendMessage(ctx.getSource(), retval.details());
            PreOperations.TryApplyCost(player, retval.cost());
        }
        else
            Helper.SendError(ctx.getSource(), retval.details());
        return 1;
    }

    public static int dyeReset(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var retval = ModelOperations.revertDyedColor(player.unwrap());

        if (retval.ok()) {
            Helper.SendMessage(ctx.getSource(), retval.details());
            PreOperations.TryApplyCost(player.unwrap(), retval.cost());
        }
        else
            Helper.SendError(ctx.getSource(), retval.details());
        return 1;
    }

    public static int unlockModel(CommandContext<ServerCommandSource> ctx) {
        var player = PreOperations.ValidateStack(ctx, 1, Check.Permission.CUSTOMIZE.getPermission());
        if (player.isErr()) {
            player.unwrapErr().BroadcastToPlayer(ctx.getSource());
            return 0;
        }

        var retval = ModelOperations.unlockModel(player.unwrap());

        if (retval.ok()) {
            Helper.SendMessage(ctx.getSource(), retval.details());
            PreOperations.TryApplyCost(player.unwrap(), retval.cost());
        }
        else
            Helper.SendError(ctx.getSource(), retval.details());

        return 1;
    }
}
