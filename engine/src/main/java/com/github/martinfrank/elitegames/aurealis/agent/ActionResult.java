package com.github.martinfrank.elitegames.aurealis.agent;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Location;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;
import com.github.martinfrank.elitegames.aurealis.adventure.TaskPredicate;

import java.util.List;

public record ActionResult(
        boolean accepted,
        String details,
        Location movedTo,
        List<TaskPredicate> grantedPredicates,
        List<Task> completedTasks,
        Chapter newChapter
) {

    public static ActionResult rejected(String reason) {
        return new ActionResult(false, reason, null, List.of(), List.of(), null);
    }

    public static ActionResult accepted(String details) {
        return new ActionResult(true, details, null, List.of(), List.of(), null);
    }

    public static ActionResult moved(Location to) {
        return new ActionResult(true, "moved to " + to.name(), to, List.of(), List.of(), null);
    }
}
