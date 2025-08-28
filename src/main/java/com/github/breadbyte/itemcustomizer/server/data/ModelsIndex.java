package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.Helper;

import java.util.*;
import java.util.stream.Collectors;

// Suppress the deprecation of Storage.HANDLER.instance()
// as this is the only way to load definitions
// It's marked as deprecated to catch external access
@SuppressWarnings("deprecation")
public class ModelsIndex {
    private final Map<NamespaceCategory, List<CustomModelDefinition>> _index = new HashMap<>();

    public static final ModelsIndex INSTANCE = new ModelsIndex();

    private ModelsIndex() { initialize(); }

    public static ModelsIndex getInstance() {
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

        for (var model : inst.CustomModels) {
            add(model);
        }
    }

    public void update_external() {
        // Update storage
        var inst = Storage.HANDLER.instance();
        if (!_index.isEmpty()) {
            inst.CustomModels = _index.values().stream().flatMap(List::stream).collect(Collectors.toList());
        } else {
            // If index is empty, clear storage too
            inst.CustomModels.clear();
        }

        Storage.HANDLER.save();
        _index.clear();
        initialize();
    }

    public void save() {
        update_external();
    }

    public void load() {
        initialize();
    }

    public void add(CustomModelDefinition model) {
        var key = new NamespaceCategory(model.getNamespace(), model.getCategory());
        _index.computeIfAbsent(key, k -> new ArrayList<>()).add(model);
    }

    public void addAll(List<CustomModelDefinition> models) {
        for (var m : models) add(m);
    }

    // Get models for a namespace+category
    public List<CustomModelDefinition> get(String namespace, String category) {
        return List.copyOf(_index.getOrDefault(new NamespaceCategory(namespace, category), List.of()));
    }

    public CustomModelDefinition get(String namespace, String category, String name) {
        var models = _index.getOrDefault(new NamespaceCategory(namespace, category), List.of());
        return models.stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    // List all namespaces
    public Set<String> namespaces() {
        return _index.keySet().stream().map(NamespaceCategory::namespace).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public CustomModelDefinition getByDestination(String namespace, String destination) {
        return _index.entrySet().stream()
                .filter(e -> e.getKey().namespace().equals(namespace))
                .flatMap(e -> e.getValue().stream())
                .filter(m -> m.getDestination().equals(destination))
                .findFirst()
                .orElse(null);
    }

    // List categories within a namespace
    public Set<String> categories(String namespace) {
        return _index.keySet().stream()
                .filter(k -> k.namespace().equals(namespace))
                .map(NamespaceCategory::category)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<NamespaceCategory> namespaceCategories() {
        return Set.copyOf(_index.keySet());
    }

    // Optional: remove everything under a namespace
    public OperationResult removeNamespace(String namespace) {
        var count = _index.keySet().stream()
                .filter(k -> k.namespace().equals(namespace))
                .count();

        _index.keySet().removeIf(k -> k.namespace().equals(namespace));

        if (count > 0) {
            save();
            return OperationResult.ok("Removed " + count + " models for namespace: " + namespace);
        }
        else
            return OperationResult.fail("No models found for namespace: " + namespace);
    }

    // Optional: clear all
    public void clear() {
        _index.clear();
        save();
    }
}