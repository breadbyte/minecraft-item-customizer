package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.Check;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;

import java.util.List;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelOperations {

    public static int fullModelReset(CommandContext<ServerCommandSource> context) {
        revertDyedColor(context);
        removeGlint(context);
        revertModel(context);

        SendMessage(context.getSource().getPlayer(), "Model reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        return 1;
    }

    public static int applyModel(CommandContext<ServerCommandSource> context) {
        var paramItemType = String.valueOf(context.getArgument("item_type", String.class));
        var paramItemName = String.valueOf(context.getArgument("item_name", String.class));
        var defs = Cache.getInstance().getDefs(paramItemName);

        // This allows us to keep the previous behavior of using direct paths for models,
        // but also allows us to use the new namespace/path format.
        if (!paramItemName.contains("/")) {
            if (defs.isEmpty()) {
                Helper.SendMessage(context.getSource().getPlayer(), "No custom model definitions found for item: " + paramItemType + "/" + paramItemName, SoundEvents.ENTITY_VILLAGER_NO);
                return 0;
            }
        }

        var customModel = defs.get();

        var paramNamespace = customModel.getNamespace();
        var paramPath = customModel.getDestination();
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
            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(),List.of(),List.of(), List.of(paramDyeColor)));
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

        Helper.SendMessage(player, "Model " + paramItemName + " applied!", SoundEvents.BLOCK_ANVIL_USE);
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
        var defaultComponents = playerItem.getItem().getDefaultStack().getComponents();
        var defaultItemModel = defaultComponents.get(DataComponentTypes.ITEM_MODEL);
        var defaultEquippable = defaultComponents.get(DataComponentTypes.EQUIPPABLE);

        // Check if the item is currently using the default model.
        // If it is, do nothing, since we're already using the default model.
        if (playerItem.getComponents().get(DataComponentTypes.ITEM_MODEL) == defaultItemModel ||
                playerItem.getComponents().get(DataComponentTypes.ITEM_MODEL) == null) {

            Helper.SendMessage(player, "This item is already using the default model!", SoundEvents.ENTITY_VILLAGER_NO);
            return 0;
        }

        // Remove the current model component otherwise.
        playerItem.remove(DataComponentTypes.ITEM_MODEL);
        playerItem.remove(DataComponentTypes.CUSTOM_NAME);
        playerItem.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
        playerItem.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);

        // Set the item model to the default model.
        playerItem.set(DataComponentTypes.ITEM_MODEL, defaultItemModel);

        // If we have an equippable component, we also reset it to the default.
        if (playerItem.getComponents().contains(DataComponentTypes.EQUIPPABLE)) {

            if (defaultComponents.contains(DataComponentTypes.EQUIPPABLE))
                playerItem.set(DataComponentTypes.EQUIPPABLE, defaultEquippable);
            else
                playerItem.remove(DataComponentTypes.EQUIPPABLE);
        }

        // Ensure component parity with default stack
        if (playerItem.getComponents().size() != defaultComponents.size()) {
            // Nuke all components that _we_ modified if the component size doesn't match

            // Remove custom anything that doesn't normally exist on the item
            playerItem.remove(DataComponentTypes.CUSTOM_NAME);
            playerItem.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
            playerItem.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);

            // Dangerous, but set dyes to default (if it exists)
            if (defaultComponents.get(DataComponentTypes.DYED_COLOR) == null) {
                playerItem.remove(DataComponentTypes.DYED_COLOR);
            } else
                playerItem.set(DataComponentTypes.DYED_COLOR, defaultComponents.get(DataComponentTypes.DYED_COLOR));

            // Set these to default
            playerItem.set(DataComponentTypes.ITEM_MODEL, defaultItemModel);

            if (defaultComponents.get(DataComponentTypes.EQUIPPABLE) == null) {
                playerItem.remove(DataComponentTypes.EQUIPPABLE);
            } else
                playerItem.set(DataComponentTypes.EQUIPPABLE, defaultEquippable);

            if (playerItem.getComponents().size() != defaultComponents.size()) {
                // If we still don't match, something's wrong
                ItemCustomizer.LOGGER.warn("Item components out of sync after reset! Current:");
                ItemCustomizer.LOGGER.warn(playerItem.getComponents().toString());
                ItemCustomizer.LOGGER.warn("Item components out of sync after reset! Default:");
                ItemCustomizer.LOGGER.warn(defaultComponents.toString());

                Helper.SendMessage(player, "Warning: Item cannot be fully reset. Item may not stack as expected. Check logs for details.", SoundEvents.ENTITY_VILLAGER_NO);
            }
            else
                Helper.SendMessage(player, "Warning: Item components out of sync. Item may not stack as expected. Check logs for details.", SoundEvents.ENTITY_VILLAGER_NO);
        } else
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
