package com.github.martinfrank.elitegames.aurealis.adventure;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Chapter (
        String id,
        int position,
        String name,
        String description,
        String aiHints,
        Location startLocation,
        String startTime,
        List<Location> locations,
        List<Person> persons,
        List<Item> items,
        List<Task> tasks)
        implements Comparable<Chapter> {

    @Override
    public int compareTo(@NotNull Chapter c) {
        return Integer.compare(position, c.position());
    }
}
