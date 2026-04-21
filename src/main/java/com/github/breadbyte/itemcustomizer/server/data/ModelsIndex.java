package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Helper;
import com.github.breadbyte.itemcustomizer.server.util.Reason;
import com.github.breadbyte.itemcustomizer.server.util.Result;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ModelsIndex {

    private final PatriciaTrie<CustomModelDefinition> _index = new PatriciaTrie<>();
    private final Map<String, String> _namespaceUrls = new HashMap<>();

    private final Map<String, Set<String>> dirIndex = new HashMap<>();
    private final Map<String, Set<String>> queryCache = new HashMap<>();
    private final Map<String, Set<String>> categoryCache = new HashMap<>();
    private final Map<String, List<String>> subcategoryCache = new HashMap<>();

    public static ModelsIndex INSTANCE;

    private boolean initialized = false;

    private ModelsIndex() { }

    public static ModelsIndex testHarness() {
        return new ModelsIndex();
    }

    public static synchronized ModelsIndex getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModelsIndex();
            INSTANCE.initialize();
        }
        return INSTANCE;
    }

    public void initialize() {
        if (initialized) return;
        initialized = true;

        queryCache.clear();
        subcategoryCache.clear();

        Helper.tryLoadStorage();
        var inst = Storage.HANDLER.instance();

        if (inst.NamespaceUrls != null) {
            // Normalize keys when loading from storage
            inst.NamespaceUrls.forEach((k, v) -> _namespaceUrls.put(k.toLowerCase(), v));
        }

        if (inst.CustomModels == null || inst.CustomModels.isEmpty()) return;

        for (var model : inst.CustomModels) {
            add(model);
            categoryCache.computeIfAbsent(model.getNamespace(), k -> new HashSet<>()).add(model.getModelPath().getCategory());
        }
    }

    public void update_external() {
        var inst = Storage.HANDLER.instance();

        // Take all the data that we currently have in the trie and put it back into the storage instance for saving
        var all = new ArrayList<>(_index.values());

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

    public void add(CustomModelDefinition model) {
        _index.putIfAbsent(model.toString(), model);
        queryCache.clear();
        subcategoryCache.clear();
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

    /**
     * Queries the trie using the combined "category/path/to/item" string.
     */

    public List<CustomModelDefinition> get(ModelPath path) {
        String searchKey = path.toString();

        // 1. Check or populate the query cache for keys matching this prefix
        Set<String> matchingKeys;
        if (queryCache.containsKey(searchKey)) {
            matchingKeys = queryCache.get(searchKey);
        } else {
            // Set a breakpoint here to debug the PatriciaTrie prefix search
            matchingKeys = new HashSet<>(_index.prefixMap(searchKey).keySet());
            queryCache.put(searchKey, matchingKeys);
        }

        List<CustomModelDefinition> results = new ArrayList<>();

        // 2. Iterate through the matching keys and aggregate models
        for (String key : matchingKeys) {
            CustomModelDefinition model = _index.get(key);
            if (model != null) {
                // Set a breakpoint here to inspect specific lists of models found in the trie
                results.add(model);
            }
        }

        return results;
    }

    // This method is meant to retrieve entries that are one segment deeper than the given path.

    // The depth looks like this:
    // namespace:path/to/model/subpath/abcd
    // 0        :1   /2 /3    /4      /5
    public List<CustomModelDefinition> getEntriesAt(ModelPath path) {
        String currentString = path.toString();
        var results = _index.prefixMap(currentString);

        return new ArrayList<>(results.values());
    }

    public static final int depthOf(String key) {
        int depth = 0;
        for (char c : key.toCharArray())
            if (c == '/' || c == ':') depth++;
        return depth;
    }

    public List<String> __internalAutocomplete(String input) {
        Set<String> results = new TreeSet<>();

        var index_entries = getEntriesAt(ModelPath.of(input));

        // Find the boundary of the current search level (after the last ':' or '/')
        int lastSeparator = Math.max(input.lastIndexOf(':'), input.lastIndexOf('/'));
        int startIdx = lastSeparator + 1;

        // Check for subdirectories in the subtree
        for (CustomModelDefinition entry : index_entries) {
            String fullPath = entry.toString();

            // We check two potential starting points for suggestions:
            // 1. Completion: From the last separator (e.g., 'blo' -> 'blocks')
            // 2. Peeking: Inside the current input if it's a directory (e.g., 'blocks' -> 'stone')
            
            int[] potentialStarts = { startIdx, input.length() + 1 };
            
            for (int start : potentialStarts) {
                if (start < 0 || start >= fullPath.length()) continue;
                
                // If we're peeking inside, ensure the character at the input boundary was a separator
                if (start == input.length() + 1) {
                    char sep = fullPath.charAt(input.length());
                    if (sep != ':' && sep != '/') continue;
                }

                String remainder = fullPath.substring(start);
                if (remainder.isEmpty()) continue;

                int nextSlash = remainder.indexOf('/');
                int nextColon = remainder.indexOf(':');
                int endIdx = -1;

                if (nextSlash != -1 && nextColon != -1) endIdx = Math.min(nextSlash, nextColon);
                else if (nextSlash != -1) endIdx = nextSlash;
                else if (nextColon != -1) endIdx = nextColon;

                results.add(endIdx != -1 ? remainder.substring(0, endIdx) : remainder);
            }
        }
        return new ArrayList<>(results);
    }

    /**
     * @deprecated Use a more structured path resolution. This is scheduled for removal.
     */
    @Deprecated
    public String getCategory(String path) {
        // Given namespace:category/subcategory/path, this returns category
        int colonIndex = path.indexOf(':');
        int firstSlash = path.indexOf('/');

        // If there's no slash, or the first slash is part of the namespace (before the colon), there's no category
        if (firstSlash == -1 || (colonIndex != -1 && firstSlash < colonIndex)) {
            return "";
        }
        return (colonIndex != -1) ? path.substring(colonIndex + 1, firstSlash) : path.substring(0, firstSlash);
    }

    /**
     * Retrieves a flat list of categories for a given namespace.
     * @deprecated This method is part of a legacy pathing system and is scheduled for removal.
     */
    @Deprecated
    public List<String> getSubcategories(String namespace) {
//        if (subcategoryCache.containsKey(namespace)) {
//            return subcategoryCache.get(namespace);
//        }

        String nsPrefix = namespace + ":";
        Set<String> distinctCategories = new HashSet<>();
        Set<String> allKeys = _index.keySet();

        for (String key : allKeys) {
            // Breakpoint here to inspect every key in the trie
            if (key.startsWith(nsPrefix)) {
                String category = getCategory(key);
                
                // Breakpoint here to see which categories are being extracted
                if (category != null && !category.isEmpty()) {
                    distinctCategories.add(category);
                }
            }
        }

        List<String> result = new ArrayList<>(distinctCategories);
        subcategoryCache.put(namespace, result);
        return result;
    }

    public List<String> getNamespaces() {
        return new ArrayList<>(_index.keySet().stream()
                .map(key -> {
                    int colonIndex = key.indexOf(':');
                    return (colonIndex != -1) ? key.substring(0, colonIndex) : "";
                })
                .filter(ns -> !ns.isEmpty())
                .collect(Collectors.toSet()));
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
}