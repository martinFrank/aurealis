package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTracker {

    private final Chapter chapter;
    private Map<Task, Task.State> tasks = new HashMap<>();

    public TaskTracker(Chapter current, Permissions permissions) {
        this.chapter = current;

        for(Task task: chapter.tasks()){
            Task.State state = task.getState(permissions);
            tasks.put(task, state);
        }
    }

    //welche tasks sind aktuell in progress?
    public List<Task> getCurrentTasks() {
        return tasks.entrySet().stream()
                .filter(e -> e.getValue() == Task.State.IN_PROGRESS)
                .map(Map.Entry::getKey)
                .toList();
    }

    //welche tasks wären bei diesen permission in progress?
    public List<Task> getCurrentTasks(Permissions permissions) {
        return tasks.keySet().stream()
                .filter(t -> t.getState(permissions) == Task.State.IN_PROGRESS)
                .toList();
    }
}
