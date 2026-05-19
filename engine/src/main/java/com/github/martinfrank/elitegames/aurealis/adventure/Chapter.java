package com.github.martinfrank.elitegames.aurealis.adventure;

import com.github.martinfrank.elitegames.aurealis.game.TaskPredicates;
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
        List<Item> items,
        List<Task> tasks,
        List<TaskPredicate> requiredTaskPredicates,
        CutScene startCutScene,
        CutScene endCutScene)
        implements Comparable<Chapter> {

    @Override
    public int compareTo(@NotNull Chapter c) {
        return Integer.compare(position, c.position());
    }

    public enum State {
        NOT_READY, OPEN, IN_PROGRESS, DONE;
    }

    public boolean isReady(TaskPredicates taskPredicates) {
        for(TaskPredicate required: requiredTaskPredicates){
            if (!taskPredicates.isGranted(required)) { //falls eine fehlt
                return false;
            }
        }
        return true;
    }
}
