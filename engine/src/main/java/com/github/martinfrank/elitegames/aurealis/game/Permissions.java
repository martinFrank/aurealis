package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Permission;

import java.util.List;

public class Permissions {

    private final List<Permission> permissions;

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
        Permission granted = new Permission(permission.id(), permission.name(), permission.description(), Permission.State.GRANTED);
        permissions.remove(permission);
        permissions.add(granted);
    }
}
