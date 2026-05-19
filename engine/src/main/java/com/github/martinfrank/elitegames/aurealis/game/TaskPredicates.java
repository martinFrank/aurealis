package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.TaskPredicate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskPredicates {

    private final List<TaskPredicate> taskPredicates;
    private final Set<TaskPredicate> granted = new HashSet<>();

    public TaskPredicates(List<TaskPredicate> taskPredicates) {
        this.taskPredicates = taskPredicates;
    }

    public TaskPredicate getById(String id) {
        return taskPredicates.stream()
                .filter(taskPredicate -> taskPredicate.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public TaskPredicate getByName(String name) {
        return taskPredicates.stream()
                .filter(taskPredicate -> taskPredicate.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void grant(TaskPredicate taskPredicate) {
        granted.add(taskPredicate);
    }

    public boolean isGranted(TaskPredicate taskPredicate) {
        return granted.contains(taskPredicate);
    }
}
