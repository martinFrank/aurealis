package com.github.martinfrank.elitegames.aurealis.adventure;

public record Item(
        String id,
        String name,
        String appearance,
        String purpose,
        String aiHints
) {
}
