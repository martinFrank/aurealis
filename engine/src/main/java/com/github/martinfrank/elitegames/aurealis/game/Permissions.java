package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Permission;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Permissions {

    private final List<Permission> permissions;
    private final Set<Permission> granted = new HashSet<>();

    public Permissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Permission getById(String id) {
        return permissions.stream()
                .filter(permission -> permission.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Permission getByName(String name) {
        return permissions.stream()
                .filter(permission -> permission.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void grant(Permission permission) {
        granted.add(permission);
    }

    public boolean isGranted(Permission permission) {
        return granted.contains(permission);
    }
}
