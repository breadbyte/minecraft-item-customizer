package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelOperations {

    public static int fullModelReset(CommandContext<ServerCommandSource> context) {
        revertModel(context);
        revertDyedColor(context);
        removeGlint(context);

        SendMessage(context.getSource().getPlayer(), "Model reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        return 1;
    }

    public static int applyModel(CommandContext<ServerCommandSource> context) {
        var paramNamespace = String.valueOf(context.getArgument("namespace", String.class));
        var paramPath = String.valueOf(context.getArgument("path", String.class));
        Integer paramDyeColor;
        Boolean changeEquippableTexture;

        // Check if these parameters exist, if not, set them to default values
        try { paramDyeColor = context.getArgument("color", Integer.class); } catch (Exception e) {
            paramDyeColor = Integer.MAX_VALUE; }
        try { changeEquippableTexture = context.getArgument("change_equippable_texture", Boolean.class); } catch (Exception e) {
            changeEquippableTexture = false;
        }


        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        // Set it to the new model
        playerItem.set(DataComponentTypes.ITEM_MODEL, Helper.String2Identifier(paramNamespace, paramPath));

        if (paramDyeColor != Integer.MAX_VALUE) {
            // Set the dyed color if provided
            playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(paramDyeColor, false));
        }

        if (changeEquippableTexture) {
            // If this item is equippable, change the model for the equippable as well.
            if (itemComps.contains(DataComponentTypes.EQUIPPABLE)) {
                // Get the first equippable component
                var equippable = itemComps.get(DataComponentTypes.EQUIPPABLE);

                // Clone the equippable, except the assetId, since we are changing the model.
                assert equippable != null;

                var eqAsset = java.util.Optional.ofNullable(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Helper.String2Identifier(paramNamespace, paramPath)));
                var newEquippable = new EquippableComponent(equippable.slot(), equippable.equipSound(), eqAsset, equippable.cameraOverlay(), equippable.allowedEntities(), equippable.dispensable(), equippable.swappable(), equippable.damageOnHurt());

                playerItem.set(DataComponentTypes.EQUIPPABLE, newEquippable);
            }
        }

        Helper.SendMessage(player, "Model " + context.getArgument("path", String.class) + " applied!", SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);

        return 1;
    }

    public static int applyModelWithDyedColor(CommandContext<ServerCommandSource> context) {
        var paramNamespace = String.valueOf(context.getArgument("namespace", String.class));
        var paramPath = String.valueOf(context.getArgument("path", String.class));
        var paramDyeColor = context.getArgument("color", Integer.class);

        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Set the item model
        playerItem.set(DataComponentTypes.ITEM_MODEL, Helper.String2Identifier(paramNamespace, paramPath));

        // Set the dyed color
        playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(paramDyeColor, false));

        // If this item is equippable, change the model for the equippable as well.
        if (playerItem.getComponents().contains(DataComponentTypes.EQUIPPABLE)) {
            // Get the first equippable component
            var equippable = playerItem.getComponents().get(DataComponentTypes.EQUIPPABLE);

            // Clone the equippable, except the assetId, since we are changing the model.
            assert equippable != null;

            var eqAsset = java.util.Optional.ofNullable(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Helper.String2Identifier(paramNamespace, paramPath)));
            var newEquippable = new EquippableComponent(equippable.slot(), equippable.equipSound(), eqAsset, equippable.cameraOverlay(), equippable.allowedEntities(), equippable.dispensable(), equippable.swappable(), equippable.damageOnHurt());

            playerItem.set(DataComponentTypes.EQUIPPABLE, newEquippable);
        }

        Helper.SendMessage(player, "Model " + context.getArgument("path", String.class) + " with color applied!", SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);
        return 1;
    }

    public static int revertModel(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();

        // Get the current item in the player's hand
        var playerItem = player.getMainHandStack();

        // Get its components
        var itemComps = playerItem.getComponents();
        itemComps.get(DataComponentTypes.ITEM_MODEL);

        // Get the default item model for the item to compare
        var defaultItemModel = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.ITEM_MODEL);
        var defaultEquippable = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.EQUIPPABLE);

        // Check if the item is currently using the default model.
        // If it is, do nothing, since we're already using the default model.
        if (playerItem.getComponents().get(DataComponentTypes.ITEM_MODEL) == defaultItemModel ||
                playerItem.getComponents().get(DataComponentTypes.ITEM_MODEL) == null) {

            Helper.SendMessage(player, "This item is already using the default model!", SoundEvents.ENTITY_VILLAGER_NO);
            return 0;
        }

        // Remove the current model component otherwise.
        playerItem.remove(DataComponentTypes.ITEM_MODEL);

        // Set the item model to the default model.
        playerItem.set(DataComponentTypes.ITEM_MODEL, defaultItemModel);

        // If we have an equippable component, we also reset it to the default.
        if (playerItem.getComponents().contains(DataComponentTypes.EQUIPPABLE)) {
            playerItem.set(DataComponentTypes.EQUIPPABLE, defaultEquippable);
        }

        Helper.SendMessage(player, "Model reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        return 1;
    }

    public static int applyGlint(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Set the shine component to true
        playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        Helper.SendMessage(player, "Glint added!", SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);
        return 1;
    }

    public static int removeGlint(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Remove the shine component
        playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);

        Helper.SendMessage(player, "Glint removed!", SoundEvents.BLOCK_ANVIL_USE);
        return 1;
    }

    public static int applyDyedColor(CommandContext<ServerCommandSource> context) {
        var paramDyeColor = context.getArgument("color", Integer.class);

        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Set it to the new model
        playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(paramDyeColor, false));


        Helper.SendMessage(player, "Color applied!", SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);
        return 1;
    }

    public static int revertDyedColor(CommandContext<ServerCommandSource> context) {
        var playerContainer = Check.TryReturnValidState(context, Check.Permission.CUSTOMIZE.getPermission());
        if (playerContainer.isEmpty())
            return 0;

        var player = playerContainer.get();
        var playerItem = player.getMainHandStack();

        // Get the default dyed color for the item
        var defaultDyedColor = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);

        // Check if the item is currently using the default dyed color.
        // If it is, do nothing, since we're already using the default dyed color.
        if (playerItem.getComponents().get(DataComponentTypes.DYED_COLOR) == defaultDyedColor ||
                playerItem.getComponents().get(DataComponentTypes.DYED_COLOR) == null) {

            Helper.SendMessage(player, "This item is already using the default color!", SoundEvents.ENTITY_VILLAGER_NO);
            return 0;
        }

        // Remove the current dyed color component otherwise.
        playerItem.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color.
        playerItem.set(DataComponentTypes.DYED_COLOR, defaultDyedColor);

        Helper.SendMessage(player, "Color reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        return 1;
    }

}
