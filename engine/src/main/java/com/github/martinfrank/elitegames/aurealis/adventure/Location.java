package com.github.martinfrank.elitegames.aurealis.adventure;

import com.github.martinfrank.elitegames.aurealis.game.TaskPredicates;

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

    public boolean isReady(TaskPredicates taskPredicates) {
        for (TaskPredicate required : requiredTaskPredicates) {
            if (!taskPredicates.isGranted(required)) {
                return false;
            }
        }
        return true;
    }
}
