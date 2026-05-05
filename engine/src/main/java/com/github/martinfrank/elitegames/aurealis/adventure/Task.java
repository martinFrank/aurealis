package com.github.martinfrank.elitegames.aurealis.adventure;

import java.util.List;

public record Task(
        String id,
        String description, //what
        String purpose, //why
        boolean required,
        List<Permission> requiredPermissions,
        List<Permission> grantedPermissions) {
}
