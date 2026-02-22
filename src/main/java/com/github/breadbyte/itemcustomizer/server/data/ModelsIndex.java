package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Helper;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ModelsIndex {

    // namespace -> PatriciaTrie<category path -> models>
    private final Map<String, PatriciaTrie<List<CustomModelDefinition>>> _index = new HashMap<>();

    public static ModelsIndex INSTANCE;

    private boolean initialized = false;

    private ModelsIndex() { }

    public static ModelsIndex testHarness() {
        return new ModelsIndex();
    }

    public static ModelsIndex getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModelsIndex();
            INSTANCE.initialize();
        }
        return INSTANCE;
    }

    public void initialize() {
        if (initialized) return;
        initialized = true;

        Helper.tryLoadStorage();
        var inst = Storage.HANDLER.instance();

        if (inst.CustomModels == null || inst.CustomModels.isEmpty()) return;

        for (var model : inst.CustomModels) {
            add(model);
        }
    }

    public void update_external() {
        var inst = Storage.HANDLER.instance();
        var all = getAll();
        if (!all.isEmpty()) {
            inst.CustomModels = all;
        } else if (inst.CustomModels != null) {
            inst.CustomModels.clear();
        }

        Storage.HANDLER.save();

        _index.clear();
        initialized = false;
        initialize();
    }

    public void save() { update_external(); }
    public void load() {
        _index.clear();
        initialized = false;
        initialize();
    }

    public void add(CustomModelDefinition model) {
        _index
            .computeIfAbsent(model.getNamespace(), ns -> new PatriciaTrie<>())
            .computeIfAbsent(NormalizeSlashes(model.getCategory()), cat -> new ArrayList<>())
            .add(model);
    }

    public void addAll(List<CustomModelDefinition> models) {
        for (var m : models) add(m);
    }

    // --- Read: exact ---

    public List<CustomModelDefinition> get(String namespace, String category) {
        var trie = _index.get(namespace);
        if (trie == null) return List.of();
        return trie.getOrDefault(NormalizeSlashes(category), List.of());
    }

    public CustomModelDefinition get(String namespace, String category, String name) {
        return get(namespace, NormalizeSlashes(category)).stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Set<CustomModelDefinition> getAllShallow(String namespace, String category) {
        return Set.copyOf(get(namespace, NormalizeSlashes(category)));
    }

    /**
     * Returns all models whose category path starts with the given prefix.
     * e.g. subcategories("minecraft", "weapons") returns models in
     * "weapons", "weapons/swords", "weapons/bows", etc.
     */
    public List<CustomModelDefinition> subcategories(String namespace, String categoryPrefix) {
        var trie = _index.get(namespace);
        if (trie == null) return List.of();

        // prefixMap returns all entries whose key starts with the given prefix
        return trie.prefixMap(NormalizeSlashes(categoryPrefix)).values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Set<CustomModelDefinition> getAllRecursive(NamespaceCategory nsc) {
        return Set.copyOf(subcategories(nsc.getNamespace(), nsc.getCategory()));
    }

    public CustomModelDefinition get(NamespaceCategory nsc, String name) {
        return get(nsc.getNamespace(), nsc.getCategory(), name);
    }

    public Set<CustomModelDefinition> getAllShallow(NamespaceCategory nsc) {
        return getAllShallow(nsc.getNamespace(), nsc.getCategory());
    }

    /**
     * Returns the immediate child segment names under a parent category.
     * e.g. for "weapons/swords" and "weapons/bows", calling with "weapons"
     * returns ["swords", "bows"].
     */
    public Set<String> immediateChildren(String namespace, String parentCategory) {
        var trie = _index.get(namespace);
        if (trie == null) return Set.of();

        var normalizedParent = NormalizeSlashes(parentCategory);
        var prefix = normalizedParent.isEmpty() ? "" : normalizedParent + "/";
        return trie.prefixMap(prefix).keySet().stream()
                .map(key -> {
                    var remainder = key.substring(prefix.length());
                    var slash = remainder.indexOf('/');
                    return slash == -1 ? remainder : remainder.substring(0, slash);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> namespaces() {
        return Collections.unmodifiableSet(_index.keySet());
    }

    /** All category paths (at any depth) within a namespace. */
    public Set<NamespaceCategory> categories(String namespace) {
        var trie = _index.get(namespace);
        if (trie == null) return Set.of();
        return trie.keySet().stream()
                .map(cat -> new NamespaceCategory(namespace, cat))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<NamespaceCategory> namespaceCategories() {
        return _index.entrySet().stream()
                .flatMap(e -> e.getValue().keySet().stream()
                        .map(cat -> new NamespaceCategory(e.getKey(), cat)))
                .collect(Collectors.toUnmodifiableSet());
    }

    public OperationResult removeNamespace(String namespace) {
        if (_index.remove(namespace) != null) {
            save();
            return OperationResult.ok("Removed all models for namespace: " + namespace);
        }
        return OperationResult.fail("No models found for namespace: " + namespace);
    }

    public void clear() {
        _index.clear();
        save();
    }

    private List<CustomModelDefinition> getAll() {
        return _index.values().stream()
                .flatMap(trie -> trie.values().stream())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private String NormalizeSlashes(String s) {
        if (s == null) return "";
        var temp = s.trim();
        while (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp;
    }
}