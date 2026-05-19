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

    public void start(Chapter chapter, TaskPredicates taskPredicates) {
        //altes chapter ist done
        Chapter current = getInProgressChapter();
        if (current != null) {
            chapters.put(current, Chapter.State.DONE);
        }

        //neues chapter auf in progress
        chapters.put(chapter, Chapter.State.IN_PROGRESS);
        taskTracker = new TaskTracker(chapter, taskPredicates);
    }

    Chapter getInProgressChapter() {
        return chapters.entrySet().stream()
                .filter(e -> e.getValue() == Chapter.State.IN_PROGRESS)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public TaskPredicateUpdateResult update(TaskPredicates taskPredicates) {
        Chapter current = getInProgressChapter();
        Optional<Chapter> candidate = chapters.entrySet().stream()
                .filter(e -> e.getValue() != Chapter.State.DONE)
                .filter(e -> e.getKey().isReady(taskPredicates))
                .findFirst()
                .map(Map.Entry::getKey);

        if (candidate.isPresent() && !candidate.get().equals(current)) {
            start(candidate.get(), taskPredicates);
            return new TaskPredicateUpdateResult(candidate.get());
        }

        List<Task> tasks = taskTracker.getCurrentTasks();
        List<Task> candidates = taskTracker.getCurrentTasks(taskPredicates);
        boolean equal = new HashSet<>(tasks).equals(new HashSet<>(candidates));
        if(!equal) {

            List<TaskChange> tc = candidates.stream().map(t -> new TaskChange(t, Task.State.NOT_READY, Task.State.IN_PROGRESS)).toList();
            return new TaskPredicateUpdateResult(tc);
        }

        return new TaskPredicateUpdateResult();
    }

    public List<Task> getInProgressTasks() {
        return taskTracker.getCurrentTasks();
    }

    public void completeTask(Task task) {
        taskTracker.complete(task);
    }
}
