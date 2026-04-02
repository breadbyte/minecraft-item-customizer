package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ModelsIndex {

    // namespace -> PatriciaTrie<category path -> models>
    private final Map<String, PatriciaTrie<List<CustomModelDefinition>>> _index = new HashMap<>();
    private final Map<String, String> _namespaceUrls = new HashMap<>();

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

        if (inst.NamespaceUrls != null) {
            _namespaceUrls.putAll(inst.NamespaceUrls);
        }

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

        inst.NamespaceUrls = new HashMap<>(_namespaceUrls);

        Storage.HANDLER.save();

        _index.clear();
        _namespaceUrls.clear();
        initialized = false;
        initialize();
    }

    public void save() { update_external(); }
    public void load() {
        _index.clear();
        _namespaceUrls.clear();
        initialized = false;
        initialize();
    }

    public void add(CustomModelDefinition model) {
        _index
            .computeIfAbsent(model.getNamespace(), ns -> new PatriciaTrie<>())
            .computeIfAbsent(model.getCategory(), cat -> new ArrayList<>())
            .add(model);
    }

    public void addAll(List<CustomModelDefinition> models) {
        for (var m : models) add(m);
    }

    public void setNamespaceUrl(String namespace, String url) {
        _namespaceUrls.put(namespace, url);
        save();
    }

    public String getNamespaceUrl(String namespace) {
        return _namespaceUrls.get(namespace);
    }

    public void clearNamespaceUrl(String namespace) {
        _namespaceUrls.remove(namespace);
        save();
    }

    public Map<String, String> getNamespaceUrls() {
        return Collections.unmodifiableMap(_namespaceUrls);
    }

    // --- Read: exact ---

    public List<CustomModelDefinition> get(String namespace, String category) {
        var trie = _index.get(namespace);
        if (trie == null) return List.of();
        return trie.getOrDefault(category, List.of());
    }

    public CustomModelDefinition get(String namespace, String category, String name) {
        return get(namespace, category).stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Set<CustomModelDefinition> getAllShallow(String namespace, String category) {
        return Set.copyOf(get(namespace, category));
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
        return trie.prefixMap(categoryPrefix).values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Set<CustomModelDefinition> getAllRecursive(ModelPath nsc) {
        return Set.copyOf(subcategories(nsc.getNamespace(), nsc.getCategory()));
    }

    public CustomModelDefinition get(ModelPath nsc, String name) {
        return get(nsc.getNamespace(), nsc.getCategory(), name);
    }

    public Set<CustomModelDefinition> getAllShallow(ModelPath nsc) {
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

        var prefix = parentCategory.isEmpty() ? "" : parentCategory + "/";
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
    public Set<ModelPath> categories(String namespace) {
        var trie = _index.get(namespace);
        if (trie == null) return Set.of();
        return trie.keySet().stream()
                .map(cat -> new ModelPath(namespace, cat))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<ModelPath> namespaceCategories() {
        return _index.entrySet().stream()
                .flatMap(e -> e.getValue().keySet().stream()
                        .map(cat -> new ModelPath(e.getKey(), cat)))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Result<String> removeNamespace(String namespace) {
        boolean removedIndex = _index.remove(namespace) != null;
        boolean removedUrl = _namespaceUrls.remove(namespace) != null;
        if (removedIndex || removedUrl) {
            save();
            return Result.ok("Removed all models for namespace: " + namespace);
        }
        return Result.err(new Reason.InternalError("No models found for namespace: " + namespace));
    }

    public void clear() {
        _index.clear();
        _namespaceUrls.clear();
        save();
    }

    private List<CustomModelDefinition> getAll() {
        return _index.values().stream()
                .flatMap(trie -> trie.values().stream())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
