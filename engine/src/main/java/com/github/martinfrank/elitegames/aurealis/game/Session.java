package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.*;
import com.github.martinfrank.elitegames.aurealis.party.Party;

import java.util.*;

public class Session {

    private final Adventure adventure;
    private final Party party;
    public final Chat chat;

    private Location partyLocation;
    private String time;
    private final TaskPredicates taskPredicates;
    private final ChapterTracker tracker;

    public Session(Adventure adventure, Party party) {
        this.adventure = adventure;
        tracker = new ChapterTracker(adventure);
        this.party = party;
        taskPredicates = new TaskPredicates(adventure.taskPredicates());
        chat = new Chat();
    }


    public void init() {
        Chapter start = tracker.getFirst();
        party.setLocation(start.startLocation());
        startChapter(start);
    }

    private void startChapter(Chapter chapter) {
        tracker.start(chapter, taskPredicates);
    }

    public TaskPredicateUpdateResult grant(TaskPredicate taskPredicate) {
        taskPredicates.grant(taskPredicate);
        return tracker.update(taskPredicates);
    }

    public Chapter getCurrentChapter() {
        return tracker.getInProgressChapter();
    }

    public List<Task> getCurrentTasks() {
        return tracker.getInProgressTasks();
    }

    public void completeTask(Task task) {
        tracker.completeTask(task);
    }

    public Party getParty() {
        return party;
    }
}
