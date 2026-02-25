package com.github.breadbyte.itemcustomizer.server.commands.impl.model.apply;

import com.github.breadbyte.itemcustomizer.main.ItemCustomizer;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.IModelApplyOperations;
import com.github.breadbyte.itemcustomizer.server.commands.defs.model.apply.ModelApplyParams;
import com.github.breadbyte.itemcustomizer.server.data.ModelsIndex;
import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;

public class ModelApplyOperations implements IModelApplyOperations {

    @Override
    public Result<String> apply(ModelApplyParams params) {
        var ns = params.identifier();
        var name = params.identifier().itemName();

        // Get the components for the currently held item
        var itemComps = params.item().getComponents();
        var item = params.item();

        // Set it to the new model
        item.set(DataComponentTypes.ITEM_MODEL, Helper.NamespaceCategoryToIdentifier(ns, name));
        var model = ModelsIndex.INSTANCE.get(ns, name);

        if (model == null)
            return Result.ok("Model " + ns + ":" + name + "applied!");

        if (model.madeBy() == null || model.madeBy().isBlank()) {
            return Result.ok("Model " + model.getName() + " applied!");
        }
        return Result.ok("Model " + model.getName() + " made by " + model.madeBy() + " applied!");
    }

    @Override
    public Result<String> reset(ModelApplyParams params) {

        // Get its components
        var item = params.item();
        var itemComps = item.getComponents();
        var defaultComponents = item.getItem().getDefaultStack().getComponents();

        // Save the trim
        var trimComponent = itemComps.get(DataComponentTypes.TRIM);

        // Clear components and apply defaults
        item.applyComponentsFrom(ComponentMap.EMPTY);
        item.applyComponentsFrom(defaultComponents);

        if (trimComponent != null) {
            item.set(DataComponentTypes.TRIM, trimComponent);
        }

        if (item.getComponents().size() != defaultComponents.size()) {

            // If we have a trim, check if the component size matches if we exclude the trim
            if (item.getComponents().contains(DataComponentTypes.TRIM)) {
                if ((item.getComponents().size() - 1) != defaultComponents.size()) {

                    // If we still don't match, something's wrong
                    ItemCustomizer.LOGGER.info("Item components out of sync after reset! Current:");
                    ItemCustomizer.LOGGER.info(item.getComponents().toString());
                    ItemCustomizer.LOGGER.info("Item components out of sync after reset! Default:");
                    ItemCustomizer.LOGGER.info(defaultComponents.toString());

                    return Result.err(new Reason.InternalError("Warning: Item components out of sync. Check logs for details."));
                }
            }
        }

        return Result.ok();
    }
}
