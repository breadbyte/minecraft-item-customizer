package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.Helper;
import com.github.breadbyte.itemcustomizer.server.data.Cache;
import com.github.breadbyte.itemcustomizer.server.data.OperationResult;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Optional;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelOperations {

    public static OperationResult applyModel(ServerPlayerEntity player, String itemType, String itemName, Integer color, Boolean changeEquippableTexture) {
        var defs = Cache.getInstance().getDefs(itemName);

        // This allows us to keep the previous behavior of using direct paths for models,
        // but also allows us to use the new namespace/path format.
        if (!itemName.contains("/")) {
            if (defs.isEmpty()) {
                return OperationResult.fail("No custom model definitions found for item: " + itemType + "/" + itemName);
            }
        }

        var paramNamespace = defs.isPresent() ? defs.get().getNamespace() : itemType;
        var paramPath = defs.isPresent() ? defs.get().getDestination() : itemName;

        // Check if these parameters exist, if not, set them to default values
        if (color == null)
            color = Integer.MIN_VALUE;

        if (changeEquippableTexture == null)
            changeEquippableTexture = false;

        var playerItem = player.getMainHandStack();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        // Set it to the new model
        playerItem.set(DataComponentTypes.ITEM_MODEL, Helper.String2Identifier(paramNamespace, paramPath));

        if (color != Integer.MIN_VALUE) {
            // Set the dyed color if provided
            playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, false));
            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(),List.of(),List.of(), List.of(color)));
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

        return OperationResult.ok("Model " + itemName + " applied!", 1);
    }

    public static OperationResult revertModel(ServerPlayerEntity player) {

        // Get the current item in the player's hand
        var playerItem = player.getMainHandStack();

        // Get its components
        var itemComps = playerItem.getComponents();
        itemComps.get(DataComponentTypes.ITEM_MODEL);

        // Get the default item model for the item to compare
        var defaultComponents = playerItem.getItem().getDefaultStack().getComponents();
        var defaultItemModel = defaultComponents.get(DataComponentTypes.ITEM_MODEL);
        var defaultEquippable = defaultComponents.get(DataComponentTypes.EQUIPPABLE);

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
                ItemCustomizer.LOGGER.info("Item components out of sync after reset! Current:");
                ItemCustomizer.LOGGER.info(playerItem.getComponents().toString());
                ItemCustomizer.LOGGER.info("Item components out of sync after reset! Default:");
                ItemCustomizer.LOGGER.info(defaultComponents.toString());

                return OperationResult.ok("Warning: Item components out of sync. Item may not stack. Check logs for details.", 1);
            }
        }

        return OperationResult.ok("Model reset to default!");
    }

    public static OperationResult applyGlint(ServerPlayerEntity player) {
        var playerItem = player.getMainHandStack();

        // Set the shine component to true
        playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        return OperationResult.ok("Glint added!", 1);
    }

    public static OperationResult removeGlint(ServerPlayerEntity player) {
        var playerItem = player.getMainHandStack();

        // Remove the shine component
        playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);

        return OperationResult.ok("Glint removed!", 1);
    }

    public static OperationResult revertDyedColor(ServerPlayerEntity player) {
        var playerItem = player.getMainHandStack();

        // Get the default dyed color for the item
        var defaultDyedColor = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);

        // Remove the current dyed color component otherwise.
        playerItem.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color.
        playerItem.set(DataComponentTypes.DYED_COLOR, defaultDyedColor);

        return OperationResult.ok("Color reset to default!", 1);
    }

}
