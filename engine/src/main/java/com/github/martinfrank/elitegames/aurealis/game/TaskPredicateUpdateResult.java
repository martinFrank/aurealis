package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;

import java.util.List;

public class TaskPredicateUpdateResult {

    public final Chapter chapter;
    public final List<TaskChange> taskChanges;

    public TaskPredicateUpdateResult() {
        this(null, null);
    }

    public TaskPredicateUpdateResult(Chapter chapter) {
        this(chapter, null);
    }
    public TaskPredicateUpdateResult(List<TaskChange> tasks) {
        this(null, tasks);
    }

    private TaskPredicateUpdateResult(Chapter chapter, List<TaskChange> taskChanges) {
        this.chapter = chapter;
        this.taskChanges = taskChanges;
    }

    public boolean hasChapterChanged(){
        return this.chapter != null;
    }

    public boolean haveTasksChanged(){
        return this.taskChanges != null;
    }

    public boolean isUnchanged(){
        return this.taskChanges == null && this.chapter == null;
    }
}
