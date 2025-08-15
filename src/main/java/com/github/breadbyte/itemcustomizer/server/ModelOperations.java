package com.github.breadbyte.itemcustomizer.server;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;

import static com.github.breadbyte.itemcustomizer.server.Helper.SendMessage;

public class ModelOperations {

    public static int applyModel(CommandContext<ServerCommandSource> context) {
        var paramNamespace = String.valueOf(context.getArgument("namespace", String.class));
        var paramPath = String.valueOf(context.getArgument("path", String.class));

        var player = performChecks(context, Check.Permission.CUSTOMIZE.getPermission());
        if (player == null) {
            return 0;
        }
        var playerItem = player.getMainHandStack();

        if (!player.isInCreativeMode()) {
            if (player.experienceLevel < 1) {
                SendMessage(player, "This command requires at least 1 experience level!", SoundEvents.ENTITY_VILLAGER_NO);
                return 0;
            }
        }

        var itemComps = playerItem.getComponents();
        itemComps.get(DataComponentTypes.ITEM_MODEL);
        playerItem.set(DataComponentTypes.ITEM_MODEL, Helper.String2Identifier(paramNamespace, paramPath));

        if (itemComps.contains(DataComponentTypes.EQUIPPABLE)) {
            // Get the first equippable component
            var equippable = itemComps.get(DataComponentTypes.EQUIPPABLE);

            // Clone the equippable, except the assetId, since we are changing the model.
            assert equippable != null;

            var eqAsset = java.util.Optional.ofNullable(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Helper.String2Identifier(paramNamespace, paramPath)));
            var newEquippable = new EquippableComponent(equippable.slot(), equippable.equipSound(), eqAsset, equippable.cameraOverlay(), equippable.allowedEntities(), equippable.dispensable(), equippable.swappable(), equippable.damageOnHurt());

            playerItem.set(DataComponentTypes.EQUIPPABLE, newEquippable);
        }

        SendMessage(player, "Model " + context.getArgument("path", String.class) + " applied!", SoundEvents.BLOCK_ANVIL_USE);
        Helper.ApplyCost(player, 1);

        return 1;
    }

    public static ServerPlayerEntity performChecks(CommandContext<ServerCommandSource> context, String permission) {
        return Check.TryReturnValidState(context, permission).get();
    }

    public static int revertModel(CommandContext<ServerCommandSource> context) {
        var player = performChecks(context, Check.Permission.CUSTOMIZE.getPermission());
        if (player == null) {
            return 0;
        }

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

            SendMessage(player, "This item is already using the default model!", SoundEvents.ENTITY_VILLAGER_NO);
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

        SendMessage(player, "Model reset to default!", SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        return 1;
    }
}
