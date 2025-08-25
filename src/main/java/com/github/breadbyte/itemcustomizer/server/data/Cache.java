package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

// Suppress the deprecation of Storage.HANDLER.instance()
// as this is the only way to load definitions
// It's marked as deprecated to catch external access
@SuppressWarnings("deprecation")
public class Cache {
    private final List<CustomModelDefinition> customModelsCache = new ArrayList<>();

    // The only thing that really matters in the end is the itemName,
    // as that is what determines the destination.
    private ArrayList<String> namespaces = new ArrayList<>();
    private ArrayList<String> itemNames = new ArrayList<>();
    private ArrayList<String> itemTypes = new ArrayList<>();
    private ArrayList<String> namespace_itemType = new ArrayList<>();
    private final HashMap<String, String> itemNameToDestinationMap = new HashMap<>();

    public static final Cache INSTANCE = new Cache();

    private Cache() { initialize(); }

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
        populateSubArrays();
    }

    public void update() {
        // Update storage
        var inst = Storage.HANDLER.instance();
        inst.CustomModels = List.copyOf(customModelsCache);
        Storage.HANDLER.save();
    }

    public void save() {
        update();
    }

    public void load() {
        initialize();
    }

    public void add(CustomModelDefinition model) {
        customModelsCache.add(model);
        namespaces.add(model.getNamespace());
        itemTypes.add(model.getItemType());
        itemNames.add(model.getItemName());
        namespace_itemType.add(model.getNamespace() + "." + model.getItemType());

        itemNameToDestinationMap.put(model.getItemName(), model.getDestination());
    }

    public void addAll(List<CustomModelDefinition> models) {
        customModelsCache.addAll(models);
        populateSubArrays();

        for (CustomModelDefinition model : models) {
            itemNameToDestinationMap.put(model.getItemName(), model.getDestination());
        }
    }

    private void populateSubArrays() {
        namespaces.addAll(customModelsCache.stream().map(CustomModelDefinition::getNamespace).distinct().toList());
        itemTypes.addAll(customModelsCache.stream().map(CustomModelDefinition::getItemType).distinct().toList());
        itemNames.addAll(customModelsCache.stream().map(CustomModelDefinition::getItemName).distinct().toList());


        // <namespace>.<itemType> in namespace_itemtype
        namespace_itemType.addAll(customModelsCache.stream()
                .map(model -> model.getNamespace() + "." + model.getItemType())
                .distinct()
                .toList());
    }

    public void clear() {
        customModelsCache.clear();
        namespaces.clear();
        itemTypes.clear();
        itemNames.clear();
        itemNameToDestinationMap.clear();
    }

    public List<CustomModelDefinition> getCustomModels() {
        return List.copyOf(customModelsCache);
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public List<String> getItemTypes() {
        return itemTypes;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public List<String> getNamespace_ItemType_s() {
        return namespace_itemType;
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

    public OperationResult removeNamespace(String namespace) {
        var count = customModelsCache.stream().filter(model -> model.getNamespace().equals(namespace));
        var removed = customModelsCache.removeIf(model -> model.getNamespace().equals(namespace));

        if (removed)
            save();
        else {
            return OperationResult.fail("No models found for namespace: " + namespace);
        }

        // Rebuild the cache
        clear();
        populateSubArrays();


        return OperationResult.ok("Removed " + count.count() + " models for namespace: " + namespace);
    }
}