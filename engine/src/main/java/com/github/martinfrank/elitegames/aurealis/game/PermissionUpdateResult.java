package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;

import java.util.List;

public class PermissionUpdateResult {

    private final Chapter chapter;
    private final List<Task> tasks;

    public PermissionUpdateResult() {
        this(null, null);
    }

    public PermissionUpdateResult(Chapter chapter) {
        this(chapter, null);
    }
    public PermissionUpdateResult(List<Task> tasks) {
        this(null, tasks);
    }

    private PermissionUpdateResult(Chapter chapter, List<Task> tasks) {
        this.chapter = chapter;
        this.tasks = tasks;
    }

    public boolean hasChapterChanged(){
        return this.chapter != null;
    }

    public boolean haveTasksChanged(){
        return this.tasks != null;
    }

    public boolean isUnchanged(){
        return this.tasks == null && this.chapter == null;
    }
}
