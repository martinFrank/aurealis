package com.github.martinfrank.elitegames.aurealis.adventure;

import java.util.List;

public record Adventure(
        String title,
        String description,
        String author,
        List<Chapter> chapters,
        List<TaskPredicate> taskPredicates,
        List<Person> persons,
        List<Item> items,
        List<Location> locations,
        List<CutScene> cutScenes
        ) {
}
