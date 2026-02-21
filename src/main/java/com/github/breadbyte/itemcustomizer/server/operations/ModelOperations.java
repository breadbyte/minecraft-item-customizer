package com.github.breadbyte.itemcustomizer.server.operations;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.commands.impl.PreOperations;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Check;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.OperationResult;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Optional;

import static com.github.breadbyte.itemcustomizer.main.ItemCustomizer.LOGGER;
import static com.github.breadbyte.itemcustomizer.server.util.AccessValidator.IsAdmin;

public class ModelOperations {

    // todo: split color from apply model (use dye command instead)
    public static OperationResult applyModel(ServerPlayerEntity player, String namespace, String category, String name, Integer color, Boolean changeEquippableTexture) {
        CustomModelDefinition defs = null;

        var model = ModelsIndex.getInstance().get(namespace, category, name);

        // Allow changing to a model that doesn't exist if we are an admin
        if (!AccessValidator.IsAdmin(player)) {
            if (model == null) {
                return OperationResult.fail("No custom model definitions found for item: " + category + ":" + name);
            }
        }

        defs = model;

        // Check if we have permissions for the specified item
        if (!AccessValidator.IsAdmin(player)) {
            if (!defs.getPermission(player)) {
                LOGGER.warn("Player {} tried to customize {}/{} with no permissions!", player.getName().getString(), category, name);
                return OperationResult.fail("Permission denied for " + category + "/" + name);
            }
        }

        // Check if these parameters exist, if not, set them to default values
        if (color == null)
            color = Integer.MIN_VALUE;

        if (changeEquippableTexture == null)
            changeEquippableTexture = false;

        var playerItem = player.getMainHandStack();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        // Set it to the new model
        playerItem.set(DataComponentTypes.ITEM_MODEL, Helper.String2Identifier(namespace, category, name));

        if (color != Integer.MIN_VALUE) {
            // Set the dyed color if provided
            playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color));
            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(), List.of(color)));
        }

        if (changeEquippableTexture) {
            // If this item is equippable, change the model for the equippable as well.
            if (itemComps.contains(DataComponentTypes.EQUIPPABLE)) {
                // Get the first equippable component
                var equippable = itemComps.get(DataComponentTypes.EQUIPPABLE);

                // Clone the equippable, except the assetId, since we are changing the model.
                assert equippable != null;

                var eqAsset = java.util.Optional.ofNullable(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Helper.String2Identifier(namespace, category, name)));
                if (eqAsset.isEmpty()) {
                    return OperationResult.fail("Failed to create equipment asset for model: " + namespace + "/" + category);
                }

                var newEquippableBuilder = EquippableComponent.builder(equippable.slot())
                        .equipSound(equippable.equipSound())
                        .model(eqAsset.get())
                        .dispensable(equippable.dispensable())
                        .swappable(equippable.swappable())
                        .damageOnHurt(equippable.damageOnHurt());

                if (equippable.cameraOverlay().isPresent())
                    newEquippableBuilder.cameraOverlay(equippable.cameraOverlay().get());
                if (equippable.allowedEntities().isPresent())
                    newEquippableBuilder.allowedEntities(equippable.allowedEntities().get());

                var newEquippable = newEquippableBuilder.build();

                playerItem.set(DataComponentTypes.EQUIPPABLE, newEquippable);
            }
        }

        return OperationResult.ok("Model " + name + " made by " + defs.getMadeBy() + " applied!", 1);
    }

    public static OperationResult revertModel(ServerPlayerEntity player) {

        // Get the current item in the player's hand
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (getPlayerItem.isErr()) {
            return OperationResult.fail("Player has no valid item in hand");
        }

        var playerItem = getPlayerItem.unwrap();

        // Get its components
        var itemComps = playerItem.getComponents();
        itemComps.get(DataComponentTypes.ITEM_MODEL);

        // Get the default item model for the item to compare
        var defaultComponents = playerItem.getItem().getDefaultStack().getComponents();
        var defaultItemModel = defaultComponents.get(DataComponentTypes.ITEM_MODEL);
        var defaultEquippable = defaultComponents.get(DataComponentTypes.EQUIPPABLE);

        // Remove the current model component otherwise.
        playerItem.remove(DataComponentTypes.ITEM_MODEL);
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

                // If we have a trim, check if the component size matches if we exclude the trim
                if (playerItem.getComponents().contains(DataComponentTypes.TRIM)) {
                    if ((playerItem.getComponents().size() - 1) != defaultComponents.size()) {

                        // If we still don't match, something's wrong
                        ItemCustomizer.LOGGER.info("Item components out of sync after reset! Current:");
                        ItemCustomizer.LOGGER.info(playerItem.getComponents().toString());
                        ItemCustomizer.LOGGER.info("Item components out of sync after reset! Default:");
                        ItemCustomizer.LOGGER.info(defaultComponents.toString());

                        return OperationResult.ok("Warning: Item components out of sync. Check logs for details.", 1);
                    }
                }
            }
        }

        return OperationResult.ok("Model reset to default!");
    }

    public static OperationResult toggleGlint(ServerPlayerEntity player) {
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (getPlayerItem.isErr()) {
            return OperationResult.fail("Player has no valid item in hand");
        }

        var playerItem = getPlayerItem.unwrap();

        // Refer to table below for logic
        // - HAS OVERRIDE? FLIP THE FLAG, EXIT EARLY
        // - IF ENCHANTED
        //  - SET OVERRIDE TO FALSE (DISABLE GLINT)
        // - IF NOT ENCHANTED
        //  - SET OVERRIDE TO TRUE (ENABLE GLINT)
        var override = playerItem.getComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        if (override != null) {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, !override);
            var replyMessage = override ? "Glint disabled!" : "Glint enabled!";
            return OperationResult.ok(replyMessage, 1);
        }

        if (playerItem.hasEnchantments()) {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            return OperationResult.ok("Glint disabled!", 1);
        } else {
            playerItem.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            return OperationResult.ok("Glint enabled!", 1);
        }
    }

    public static OperationResult applyDyedColor(ServerPlayerEntity player, @UnknownNullability Integer colorClass) {
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (getPlayerItem.isErr()) {
            return OperationResult.fail("Player has no valid item in hand");
        }

        var playerItem = getPlayerItem.unwrap();

        // Get the default dyed color for the item
        var defaultDyedColor = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);
        playerItem.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color.
        playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(colorClass));

        return OperationResult.ok("Dye set!", 1);
    }

    public static OperationResult revertDyedColor(ServerPlayerEntity player) {
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (getPlayerItem.isErr()) {
            return OperationResult.fail("Player has no valid item in hand");
        }
        var playerItem = getPlayerItem.unwrap();

        // Get the default dyed color for the item
        var defaultDyedColor = playerItem.getItem().getDefaultStack().getComponents().get(DataComponentTypes.DYED_COLOR);

        // Remove the current dyed color component otherwise.
        playerItem.remove(DataComponentTypes.DYED_COLOR);

        // Set the item dyed color to the default dyed color.
        playerItem.set(DataComponentTypes.DYED_COLOR, defaultDyedColor);

        return OperationResult.ok("Dye reset to default!", 1);
    }

    public static OperationResult getPermissionNodeFor(String itemType, String itemName) {
        String namespace;
        String category;
        CustomModelDefinition defs = null;

        // Check for the autocomplete version of the itemType, which is in the format namespace.category
        if (itemType.contains(".")) {
            namespace = itemType.split("\\.")[0];
            category = itemType.split("\\.")[1];
            defs = ModelsIndex.getInstance().get(namespace, category, itemName);

            if (defs == null) {
                return OperationResult.fail("No custom model definitions found for item: " + itemType + "/" + itemName);
            }
        } else {
            // Using the old format of itemType as category only, and itemName as the full path.
            namespace = itemType;
            category = itemName;

            return OperationResult.ok(Check.Permission.CUSTOMIZE.getPermission() + "." + namespace + category.replace("/", "."));
        }

        if (defs != null) {
            return OperationResult.ok(defs.getPermissionNode());
        } else {
            return OperationResult.fail("No custom model definitions found for item: " + itemType + "/" + itemName);
        }
    }

    public static OperationResult lockModel(ServerPlayerEntity player) {
        var playerHand = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (playerHand.isErr()) {
            return OperationResult.fail(playerHand.unwrapErr().toString());
        }

        // Get the components for the currently held item
        var playerItem = playerHand.unwrap();
        var itemComps = playerItem.getComponents();
        var playerUuid = Text.literal(player.getUuidAsString());

        if (itemComps.get(DataComponentTypes.LOCK) != null) {
            var lock = playerItem.getComponents().get(DataComponentTypes.LOCK);
            var pred = lock.predicate().components().exact();

            // Convert to ComponentChanges to get typed access
            ComponentChanges changes = pred.toChanges();

            // Read the specific component value
            Optional<? extends Text> name = changes.get(DataComponentTypes.CUSTOM_NAME);
            if (name == null) {
                return OperationResult.fail("Failed to read lock component");
            }

            if (name.isPresent()) {
                String uuid = name.get().getString();
                if (!uuid.equals(player.getUuidAsString())) {
                    try {
                        var locker = player.getEntityWorld().getServer().getPlayerManager().getPlayer(uuid).getName();
                        return OperationResult.fail("This item is locked by + " + locker + " and cannot be modified!");
                    }
                    catch (NullPointerException e) {
                        return OperationResult.fail("This item is locked by another player and cannot be modified!");
                    }
                }
            }
        } else {
            // Set it to the new model
            playerItem.set(DataComponentTypes.LOCK, new ContainerLock(new ItemPredicate.Builder()
                    .components(ComponentsPredicate.Builder.create()
                            .exact(ComponentMapPredicate.of(DataComponentTypes.CUSTOM_NAME, playerUuid))
                            .build())
                    .build()));
        }

        return OperationResult.ok("Model locked!");
    }

    public static OperationResult unlockModel(ServerPlayerEntity player) {
        var playerHand = PreOperations.TryGetValidPlayerCurrentHand(player);
        if (playerHand.isErr()) {
            return OperationResult.fail(playerHand.unwrapErr().toString());
        }
        var playerItem = playerHand.unwrap();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();
        var playerUuid = Text.literal(player.getUuidAsString());

        var lock = itemComps.get(DataComponentTypes.LOCK);

        if (lock == null)
            return OperationResult.fail("Item is not locked!");

        var pred = lock.predicate().components().exact();

        // Convert to ComponentChanges to get typed access
        ComponentChanges changes = pred.toChanges();

        // Read the specific component value
        Optional<? extends Text> name = changes.get(DataComponentTypes.CUSTOM_NAME);
        if (name == null) {
            return OperationResult.fail("Failed to read lock component");
        }

        if (name.isPresent()) {
            String uuid = name.get().getString();
            if (!uuid.equals(player.getUuidAsString())) {
                return OperationResult.fail("This item is locked by another player and cannot be modified!");
            }
        }

        return OperationResult.ok("Model unlocked!");
    }
}
