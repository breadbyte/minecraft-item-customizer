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

    private ModelsIndex() { }
    private ModelsIndex(boolean init) {
        if (init) initialize();
    }

    public static ModelsIndex testHarness() {
        return new ModelsIndex(false);
    }

    public static ModelsIndex getInstance() { if (INSTANCE == null) INSTANCE = new ModelsIndex(true); return INSTANCE; }

    public void initialize() {
        if (INSTANCE == null) INSTANCE = new ModelsIndex(true);

        Helper.tryLoadStorage();
        var inst = Storage.HANDLER.instance();

        if (inst.CustomModels.isEmpty()) return;

        for (var model : inst.CustomModels) {
            add(model);
        }
    }

    public void update_external() {
        var inst = Storage.HANDLER.instance();
        var all = getAll();
        if (!all.isEmpty()) {
            inst.CustomModels = all;
        } else {
            inst.CustomModels.clear();
        }

        Storage.HANDLER.save();
        _index.clear();
        initialize();
    }

    public void save() { update_external(); }
    public void load() { initialize(); }

    public void add(CustomModelDefinition model) {
        _index
            .computeIfAbsent(model.getNamespace(), ns -> new PatriciaTrie<>())
            .computeIfAbsent(model.getCategory(), cat -> new ArrayList<>())
            .add(model);
    }

    public void addAll(List<CustomModelDefinition> models) {
        for (var m : models) add(m);
    }

    // --- Read: exact ---

    /** All models at an exact namespace + category path. */
    public List<CustomModelDefinition> get(String namespace, String category) {
        var trie = _index.get(namespace);
        if (trie == null) return List.of();
        return trie.getOrDefault(NormalizeSlashes(category), List.of());
    }

    /** Single model by namespace + category + name. */
    public CustomModelDefinition get(String namespace, String category, String name) {
        return get(namespace, NormalizeSlashes(category)).stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Set<CustomModelDefinition> getAllShallow(String namespace, String category) {
        return Set.copyOf(get(namespace, NormalizeSlashes(category)));
    }

    // --- Read: prefix (nested categories) ---

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

    /**
     * Returns the immediate child segment names under a parent category.
     * e.g. for "weapons/swords" and "weapons/bows", calling with "weapons"
     * returns ["swords", "bows"].
     */
    public Set<String> immediateChildren(String namespace, String parentCategory) {
        var trie = _index.get(namespace);
        if (trie == null) return Set.of();

        var prefix = parentCategory.isEmpty() ? "" : parentCategory + "/";
        return trie.prefixMap(prefix).keySet().stream()
                .map(key -> {
                    var remainder = key.substring(prefix.length());
                    var slash = remainder.indexOf('/');
                    return slash == -1 ? remainder : remainder.substring(0, slash);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // --- Read: namespace/category sets ---

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

    // --- Legacy compat ---

    public CustomModelDefinition getOldNamespacePath(String namespace, String path) {
        var fullPath = namespace + ":" + path;
        var trie = _index.get(namespace);
        if (trie == null) return null;
//        return trie.values().stream()
//                .flatMap(List::stream)
//                .filter(m -> m.destination.equals(fullPath))
//                .findFirst()
//                .orElse(null);
        throw new NotImplementedException();
    }

    // --- Remove ---

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

    // --- Internal helpers ---

    private List<CustomModelDefinition> getAll() {
        return _index.values().stream()
                .flatMap(trie -> trie.values().stream())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private String NormalizeSlashes(String s) {
        // Remove trailing backslashes and whitespace
        var trimmed = s.trim().endsWith("/") ? s.trim().substring(0, s.trim().length() - 1) : s.trim();
        return trimmed;
    }

    public CustomModelDefinition get(NamespaceCategory nsc, String name) {
        return get(nsc.getNamespace(), nsc.getCategory(), name);
    }

    public Set<CustomModelDefinition> getAllShallow(NamespaceCategory nsc) {
        return getAllShallow(nsc.getNamespace(), nsc.getCategory());
    }
}