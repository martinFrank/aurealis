package com.github.martinfrank.elitegames.aurealis.adventure;

import java.util.List;

public record Adventure(
        String title,
        String description,
        String author,
        List<Chapter> chapters,
        List<Permission> permissions
        ) {
}
