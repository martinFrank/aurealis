package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;

import java.util.List;

public class PermissionUpdateResult {

    public final Chapter chapter;
    public final List<TaskChange> taskChanges;

    public PermissionUpdateResult() {
        this(null, null);
    }

    public PermissionUpdateResult(Chapter chapter) {
        this(chapter, null);
    }
    public PermissionUpdateResult(List<TaskChange> tasks) {
        this(null, tasks);
    }

    private PermissionUpdateResult(Chapter chapter, List<TaskChange> taskChanges) {
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
