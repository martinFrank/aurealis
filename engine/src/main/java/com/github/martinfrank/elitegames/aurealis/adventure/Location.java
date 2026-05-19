package com.github.martinfrank.elitegames.aurealis.adventure;

import java.util.List;

public record Location(
        String id,
        String name,
        String description,
        String aiHints,
        List<TaskPredicate> requiredTaskPredicates,
        List<LocalizedPerson> persons ) {

    public record LocalizedPerson (Person person, TaskPredicate requiredTaskPredicate) {
    }

}
