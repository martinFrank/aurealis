package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Permission;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;

import java.util.*;

public class ChapterTracker {

    private final SequencedMap<Chapter, Chapter.State> chapters = new TreeMap<>();
    private Map<Task, Task.State> tasks = new HashMap<>();

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

        //taskliste aktualisieren
        tasks.clear();
        for(Task task: chapter.tasks()){
            Task.State state = task.getState(permissions);
            tasks.put(task, state);
        }
    }

    private Chapter getInProgress() {
        return chapters.entrySet().stream().filter(e -> e.getValue() == Chapter.State.IN_PROGRESS).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public void update(Permissions permissions) {
        
    }
}
