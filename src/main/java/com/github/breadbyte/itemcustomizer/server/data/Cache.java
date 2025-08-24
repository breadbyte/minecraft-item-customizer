package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.Helper;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cache {
    private final List<CustomModelDefinition> customModelsCache = new ArrayList<>();

    // The only thing that really matters in the end is the itemName,
    // as that is what determines the destination.
    private String[] namespaces = new String[0];
    private String[] itemTypes = new String[0];
    private String[] itemNames = new String[0];
    private HashMap<String, String> itemNameToDestinationMap = new HashMap<>();

    private static final Cache INSTANCE = new Cache();

    private Cache() {}

    public static Cache getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        // Try load existing defs
        Helper.tryLoadStorage();
        var inst = Storage.HANDLER.instance();

        // No existing defs available, exit early
        if (inst.CustomModels.isEmpty()) {
            return;
        }

        // Clear cache
        clear();
        var size = inst.CustomModels.size();

        // Init
        var _namespaces = new ArrayList<String>(size);
        var _itemTypes = new ArrayList<String>(size);
        var _itemNames = new ArrayList<String>(size);

        for (CustomModelDefinition model : inst.CustomModels) {
            itemNameToDestinationMap.put(model.getItemName(), model.getDestination());
            customModelsCache.add(model);
            _namespaces.add(model.getNamespace());
            _itemTypes.add(model.getItemType());
            _itemNames.add(model.getItemName());
        }

        // Finalize
        namespaces = _namespaces.stream().distinct().toArray(String[]::new);
        itemTypes = _itemTypes.stream().distinct().toArray(String[]::new);
        itemNames = _itemNames.stream().distinct().toArray(String[]::new);
    }

    public void add(CustomModelDefinition model) {
        customModelsCache.add(model);
        populate();

        itemNameToDestinationMap.put(model.getItemName(), model.getDestination());
    }

    public void addAll(List<CustomModelDefinition> models) {
        customModelsCache.addAll(models);
        populate();

        for (CustomModelDefinition model : models) {
            itemNameToDestinationMap.put(model.getItemName(), model.getDestination());
        }
    }

    private void populate() {
        namespaces = customModelsCache.stream().map(CustomModelDefinition::getNamespace).distinct().toArray(String[]::new);
        itemTypes = customModelsCache.stream().map(CustomModelDefinition::getItemType).distinct().toArray(String[]::new);
        itemNames = customModelsCache.stream().map(CustomModelDefinition::getItemName).distinct().toArray(String[]::new);
    }

    private void clear() {
        customModelsCache.clear();
        namespaces = new String[0];
        itemTypes = new String[0];
        itemNames = new String[0];
        itemNameToDestinationMap.clear();
    }

    public List<CustomModelDefinition> getCustomModelsCache() {
        return List.copyOf(customModelsCache);
    }

    public String[] getNamespaces() {
        return namespaces;
    }

    public String[] getItemTypes() {
        return itemTypes;
    }

    public String[] getItemNames() {
        return itemNames;
    }

    public String getDestination(String itemName) {
        return itemNameToDestinationMap.get(itemName);
    }

    public Optional<CustomModelDefinition> getDefs(String itemName) {
        // If not found, return an empty Optional
        return customModelsCache.stream()
                .filter(model -> model.getItemName().equals(itemName))
                .findFirst()
                .or(Optional::empty);
    }
}