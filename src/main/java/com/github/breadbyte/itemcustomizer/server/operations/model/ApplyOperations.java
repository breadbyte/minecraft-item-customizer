package com.github.breadbyte.itemcustomizer.server.operations.model;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.commands.dispatcher.PreOperations;
import com.github.breadbyte.itemcustomizer.server.commands.registrar.commands.model.ModelApplyCommand;
import com.github.breadbyte.itemcustomizer.server.data.CustomModelDefinition;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.data.NamespaceCategory;
import com.github.breadbyte.itemcustomizer.server.util.AccessValidator;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.xml.stream.events.Namespace;
import java.util.List;

public class ApplyOperations {
    public static Result<String> applyModelPath(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var namespace = String.valueOf(ctx.getArgument(ModelApplyCommand.NAMESPACE_ARGUMENT, String.class));
        var category = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_CATEGORY_ARGUMENT, String.class));
        String name;
        NamespaceCategory ns;

        if (!category.contains("/")) {
            ns = new NamespaceCategory(namespace, "");
            name = category;
        } else {
            // Get the last item of the path, this is the item name, the rest is the category.
            // For example, if we have a path of "old/sword/model", the name is "model" and the category is "old/sword".
            if (category.endsWith("/")) category = category.substring(0, category.length() - 1);
            name = category.split("/")[category.split("/").length - 1];
            ns = new NamespaceCategory(namespace, category.substring(0, category.lastIndexOf("/")));
        }

        // Arguments that don't necessarily exist
        Integer color = null;
        Boolean changeEquippable = null;
        try {
            color = ctx.getArgument(ModelApplyCommand.COLOR_ARGUMENT, Integer.class);
        } catch (Exception ignored) {
        }
        try {
            changeEquippable = ctx.getArgument(ModelApplyCommand.EQUIPMENT_TEXTURE_ARGUMENT, Boolean.class);
        } catch (Exception ignored) {
        }

        CustomModelDefinition m = new CustomModelDefinition(ns, name, "");

        return applyModel(player, m, color, changeEquippable);
    }

    public static Result<String> applyModelModern(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var namespace = String.valueOf(ctx.getArgument(ModelApplyCommand.NAMESPACE_ARGUMENT, String.class));
        var category = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_CATEGORY_ARGUMENT, String.class));
        var name = String.valueOf(ctx.getArgument(ModelApplyCommand.ITEM_NAME_ARGUMENT, String.class));

        // Arguments that don't necessarily exist, set defaults if they don't
        Integer color = null;
        Boolean changeEquippable = null;
        try {
            color = ctx.getArgument(ModelApplyCommand.COLOR_ARGUMENT, Integer.class);
        } catch (Exception ignored) {
        }
        try {
            changeEquippable = ctx.getArgument(ModelApplyCommand.EQUIPMENT_TEXTURE_ARGUMENT, Boolean.class);
        } catch (Exception ignored) {
        }

        NamespaceCategory ns;

        if (name.contains("/")) {
           var split = name.split("/");
           category = name.substring(0, name.lastIndexOf("/"));
           name = split[split.length - 1];
        }

        ns = new NamespaceCategory(namespace, category);
        CustomModelDefinition m = ModelsIndex.getInstance().get(ns, name);

        if (m == null) {
            if (!AccessValidator.IsAdmin(player)) {
                return Result.err(new Reason.InternalError("No custom model definition found for model: " + ns.withItemName(name)));
            }

            // Force anyway if we're admin
            m = new CustomModelDefinition(ns, name, "");
        }

        return applyModel(player, m, color, changeEquippable);
    }

    static Result<String> applyModel(ServerPlayerEntity player, CustomModelDefinition model, Integer color, Boolean changeEquippable) {
        var ns = model.getNamespaceCategory();
        var name = model.getName();
        var playerItem = PreOperations.TryGetValidPlayerCurrentHand(player).unwrap();

        // Get the components for the currently held item
        var itemComps = playerItem.getComponents();

        // Set it to the new model
        playerItem.set(DataComponentTypes.ITEM_MODEL, Helper.NamespaceCategoryToIdentifier(ns, name));

        if (color != null) {
            // Set the dyed color if provided
            playerItem.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color));
            playerItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of(), List.of(color)));
        }

        if (changeEquippable != null) {
            if (changeEquippable) {
                // If this item is equippable, change the model for the equippable as well.
                if (itemComps.contains(DataComponentTypes.EQUIPPABLE)) {
                    // Get the first equippable component
                    var equippable = itemComps.get(DataComponentTypes.EQUIPPABLE);

                    // Clone the equippable, except the assetId, since we are changing the model.
                    assert equippable != null;

                    var eqAsset = java.util.Optional.ofNullable(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Helper.NamespaceCategoryToIdentifier(ns, name)));
                    if (eqAsset.isEmpty()) {
                        return Result.err(new Reason.InternalError("Failed to create equipment asset for model: " + ns.withItemName(name)));
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
        }

        if (model.getMadeBy() == null || model.getMadeBy().isBlank()) {
            return Result.ok("Model " + model.getName() + " applied!");
        }
        return Result.ok("Model " + model.getName() + " made by " + model.getMadeBy() + " applied!");
    }

    public static Result<Void> resetModel(ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        var getPlayerItem = PreOperations.TryGetValidPlayerCurrentHand(player);
        var playerItem = getPlayerItem.unwrap();

        // Get its components
        var itemComps = playerItem.getComponents();
        var defaultComponents = playerItem.getItem().getDefaultStack().getComponents();

        // Save the trim
        var trimComponent = itemComps.get(DataComponentTypes.TRIM);

        // Clear components and apply defaults
        playerItem.applyComponentsFrom(ComponentMap.EMPTY);
        playerItem.applyComponentsFrom(defaultComponents);

        if (trimComponent != null) {
            playerItem.set(DataComponentTypes.TRIM, trimComponent);
        }

        if (playerItem.getComponents().size() != defaultComponents.size()) {

            // If we have a trim, check if the component size matches if we exclude the trim
            if (playerItem.getComponents().contains(DataComponentTypes.TRIM)) {
                if ((playerItem.getComponents().size() - 1) != defaultComponents.size()) {

                    // If we still don't match, something's wrong
                    ItemCustomizer.LOGGER.info("Item components out of sync after reset! Current:");
                    ItemCustomizer.LOGGER.info(playerItem.getComponents().toString());
                    ItemCustomizer.LOGGER.info("Item components out of sync after reset! Default:");
                    ItemCustomizer.LOGGER.info(defaultComponents.toString());

                    return Result.err(new Reason.InternalError("Warning: Item components out of sync. Check logs for details."));
                }
            }
        }

        return Result.ok();
    }
}
