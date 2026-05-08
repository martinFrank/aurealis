package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;

import java.util.*;

public class ChapterTracker {

    private final SequencedMap<Chapter, Chapter.State> chapters = new TreeMap<>();
    private TaskTracker taskTracker;

    public ChapterTracker(Adventure adventure) {
        adventure.chapters().sort(null);
        for(Chapter chapter : adventure.chapters()) {
            chapters.put(chapter, Chapter.State.OPEN);
        }
    }

    public Chapter getFirst() {
        return chapters.firstEntry().getKey();
    }

    public void start(Chapter chapter, Permissions permissions) {
        //altes chapter ist done
        Chapter current = getInProgress();
        if (current != null) {
            chapters.put(current, Chapter.State.DONE);
        }

        //neues chapter auf in progress
        chapters.put(chapter, Chapter.State.IN_PROGRESS);
        taskTracker = new TaskTracker(chapter, permissions);
    }

    private Chapter getInProgress() {
        return chapters.entrySet().stream()
                .filter(e -> e.getValue() == Chapter.State.IN_PROGRESS)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public PermissionUpdateResult update(Permissions permissions) {
        Chapter current = getInProgress();
        Optional<Chapter> candidate = chapters.entrySet().stream()
                .filter(e -> e.getValue() != Chapter.State.DONE)
                .filter(e -> e.getKey().isReady(permissions))
                .findFirst()
                .map(Map.Entry::getKey);

        if (candidate.isPresent() && !candidate.get().equals(current)) {
            start(candidate.get(), permissions);
            return new PermissionUpdateResult(candidate.get());
        }

        List<Task> tasks = taskTracker.getCurrentTasks();
        List<Task> candidates = taskTracker.getCurrentTasks(permissions);
        boolean equal = new HashSet<>(tasks).equals(new HashSet<>(candidates));
        if(!equal) {
            return new PermissionUpdateResult(candidates);
        }

        return new PermissionUpdateResult();
    }
}
