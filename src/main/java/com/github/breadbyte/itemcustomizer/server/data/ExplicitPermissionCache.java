package com.github.breadbyte.itemcustomizer.server.data;

import com.github.breadbyte.itemcustomizer.server.util.Helper;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

// This class stores explicit permissions- so we don't have to check it
// Ideally, we would check this in the ModelsIndex itself, but due to time constraints,
public class ExplicitPermissionCache {

    public Map<String, List<String>> namespaceUsers = new HashMap<>();
    public Map<String, List<CustomModelDefinition>> modelUsers = new HashMap<>();

    public static ExplicitPermissionCache INSTANCE;
    private boolean initialized = false;
    ExplicitPermissionCache() { }

    public static ExplicitPermissionCache testHarness() {
        return new ExplicitPermissionCache();
    }
    public static synchronized ExplicitPermissionCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExplicitPermissionCache();
            INSTANCE.initialize();
        }
        return INSTANCE;
    }

    public void initialize() {
        if (initialized) return;
        initialized = true;

        Helper.tryLoadStorage();
        var inst = Storage.HANDLER.instance();

        if (inst.PlayersWithExplicitModelPermission != null) {
            inst.PlayersWithExplicitModelPermission.forEach((k, v) -> modelUsers.put(k.toLowerCase(), v));
        }

        if (inst.PlayersWithExplicitNamespacePermission != null) {
            inst.PlayersWithExplicitNamespacePermission.forEach((k, v) -> namespaceUsers.put(k.toLowerCase(), v));
        }
    }

    public void AddUserToModel(PlayerEntity player, CustomModelDefinition model) {
        var list = modelUsers.get(player.getUuidAsString());

        if (list == null) {
            // create new then refresh
            modelUsers.put(player.getUuidAsString(), Collections.singletonList(model));
            AddUserToNamespace(player, model.getNamespace());
            return;
        }

        list.add(model);
        AddUserToNamespace(player, model.getNamespace());
    }

    public void AddUserToNamespace(PlayerEntity player, String namespace) {
        var list = namespaceUsers.get(player.getUuidAsString());

        if (list == null) {
            namespaceUsers.put(player.getUuidAsString(), Collections.singletonList(namespace));
            return;
        }

        list.add(namespace);
    }

    public void RemoveUserFromModel(PlayerEntity player, CustomModelDefinition model) {
        var list = modelUsers.get(player.getUuidAsString());

        if (list == null) { return; }
        list.remove(model);

        // Check if user still has models that use the namespace
        // if not, then remove the namespace to prevent pollution
        var hasNamespace = list.parallelStream().anyMatch(x -> x.getNamespace().equals(model.getNamespace()));
        if (!hasNamespace) { RemoveNamespaceFromUser(player, model.getNamespace()); }
    }

    public void RemoveNamespaceFromUser(PlayerEntity player, String namespace)    {
        var list = namespaceUsers.get(player.getUuidAsString());
        if (list == null) { return; }

        list.remove(namespace);
    }


    public List<CustomModelDefinition> GetModelsForUser(PlayerEntity player) {
        if (modelUsers.get(player.getUuidAsString()) == null)
            return Collections.emptyList();

        return modelUsers.get(player.getUuidAsString());
    }

    public List<String> GetNamespacesForUser(PlayerEntity player) {
        if (namespaceUsers.get(player.getUuidAsString()) == null)
            return Collections.emptyList();

        return namespaceUsers.get(player.getUuidAsString());
    }
}
