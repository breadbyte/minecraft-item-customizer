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
            // Normalize keys when loading from storage
            inst.NamespaceUrls.forEach((k, v) -> _namespaceUrls.put(k.toLowerCase(), v));
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

        // Store normalized keys
        inst.NamespaceUrls = _namespaceUrls.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue));

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

    /**
     * Customizes how the namespace is stored as a key in the outer map.
     */
    private String toNamespaceKey(String namespace) {
        return namespace.toLowerCase(Locale.ROOT);
    }

    /**
     * Customizes how the category path is stored as a key in the PatriciaTrie.
     */
    private String toCategoryKey(String category) {
        return category.toLowerCase(Locale.ROOT);
    }

    /**
     * Combines category and name into a single trie key.
     */
    private String toPathKey(String category, String name) {
        String normalizedCat = toCategoryKey(category);
        if (normalizedCat.isEmpty()) return name.toLowerCase(Locale.ROOT);
        return normalizedCat + "/" + name.toLowerCase(Locale.ROOT);
    }

    public void add(CustomModelDefinition model) {
        _index.computeIfAbsent(toNamespaceKey(model.getNamespace()), ns -> new PatriciaTrie<>())
                .computeIfAbsent(toPathKey(model.getCategory(), model.getName()), k -> new ArrayList<>())
                .add(model);
    }

    public void addAll(List<CustomModelDefinition> models) {
        for (var m : models) add(m);
    }

    public void setNamespaceUrl(String namespace, String url) {
        _namespaceUrls.put(toNamespaceKey(namespace), url);
        save();
    }

    public String getNamespaceUrl(String namespace) {
        return _namespaceUrls.get(toNamespaceKey(namespace));
    }

    public void clearNamespaceUrl(String namespace) {
        _namespaceUrls.remove(toNamespaceKey(namespace));
        save();
    }

    public Map<String, String> getNamespaceUrls() {
        return Collections.unmodifiableMap(_namespaceUrls);
    }

    // --- Read: exact ---

    /**
     * Queries the trie using the combined "category/path/to/item" string.
     */
    public List<CustomModelDefinition> getByPath(String namespace, String fullPath) {
        var trie = _index.get(toNamespaceKey(namespace));
        if (trie == null) return List.of();
        return trie.getOrDefault(fullPath.toLowerCase(Locale.ROOT), List.of());
    }

    public List<CustomModelDefinition> get(String namespace, String category) {
        var trie = _index.get(toNamespaceKey(namespace));
        if (trie == null) return List.of();

        String prefix = toCategoryKey(category) + "/";
        return trie.prefixMap(prefix).entrySet().stream()
                .filter(e -> !e.getKey().substring(prefix.length()).contains("/"))
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
    }

    public List<CustomModelDefinition> getPartialCategoryMatch(String namespace, String category) {
        return subcategories(namespace, category);
    }

    public List<CustomModelDefinition> getPartialMatch(String namespace, String category, String name) {
        return get(namespace, category).stream()
                .filter(m -> m.getName().toLowerCase().startsWith(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<CustomModelDefinition> getPartialPathMatch(String namespace, String categoryPrefix, String name) {
        return subcategories(namespace, categoryPrefix).stream()
                .filter(m -> m.getModelPath().getFullPath().toLowerCase().startsWith(categoryPrefix + "/" + name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public CustomModelDefinition get(String namespace, String category, String name) {
        var list = getByPath(namespace, toPathKey(category, name));
        if (list.isEmpty()) return null;
        return list.get(0);
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
        var trie = _index.get(toNamespaceKey(namespace));
        if (trie == null) return List.of();

        var prefix = toCategoryKey(categoryPrefix);
        prefix = prefix.isEmpty() ? "" : prefix + "/";
        return trie.prefixMap(prefix).values().stream()
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
        var trie = _index.get(toNamespaceKey(namespace));
        if (trie == null) return Set.of();

        var prefix = toCategoryKey(parentCategory);
        prefix = prefix.isEmpty() ? "" : prefix + "/";
        String finalPrefix = prefix;
        return trie.prefixMap(prefix).keySet().stream()
                .map(key -> {
                    var remainder = key.substring(finalPrefix.length());
                    var slash = remainder.indexOf('/');
                    return slash == -1 ? remainder : remainder.substring(0, slash);
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> partialChildren(String namespace, String parentCategory, String partial) {
        var trie = _index.get(toNamespaceKey(namespace));
        if (trie == null) return Set.of();

        return trie.prefixMap(toCategoryKey(parentCategory) + "/" + partial.toLowerCase(Locale.ROOT)).keySet();


//        var prefix = parentCategory.toLowerCase(); // Normalize parent category
//        prefix = prefix.isEmpty() ? "" : prefix + "/";
//        String finalPrefix = prefix;
//        return trie.prefixMap(prefix).keySet().stream()
//                .map(key -> {
//                    var remainder = key.substring(finalPrefix.length());
//                    var slash = remainder.indexOf('/');
//                    return slash == -1 ? remainder : remainder.substring(0, slash);
//                })
//                .filter(s -> !s.isEmpty() && s.toLowerCase().startsWith(partial.toLowerCase())) // Normalize partial
//                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> namespaces() {
        return Collections.unmodifiableSet(_index.keySet());
    }

    /** All category paths (at any depth) within a namespace. */
    public Set<ModelPath> categories(String namespace) {
        var trie = _index.get(toNamespaceKey(namespace));
        if (trie == null) return Set.of();
        return trie.values().stream()
                .flatMap(List::stream)
                .map(m -> new ModelPath(toNamespaceKey(m.getNamespace()), m.getCategory()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<ModelPath> namespaceCategories() {
        return _index.entrySet().stream()
                .flatMap(e -> e.getValue().keySet().stream()
                        .map(cat -> new ModelPath(e.getKey(), cat)))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Result<String> removeNamespace(String namespace) {
        boolean removedIndex = _index.remove(toNamespaceKey(namespace)) != null;
        boolean removedUrl = _namespaceUrls.remove(toNamespaceKey(namespace)) != null;
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
