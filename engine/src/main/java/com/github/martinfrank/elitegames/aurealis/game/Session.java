package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.*;
import com.github.martinfrank.elitegames.aurealis.party.Party;

import java.util.*;

public class Session {

    private final Adventure adventure;
    private final Party party;

    private Location partyLocation;
    private String time;
    private List<Permission> permissions;
    private final ChapterTracker tracker;

    public Session(Adventure adventure, Party party) {
        this.adventure = adventure;
        tracker = new ChapterTracker(adventure);
        this.party = party;
        permissions = new ArrayList<>(adventure.permissions());
    }


    public void init() {
        Chapter start = tracker.getFirst();
        startChapter(start);
    }

    private void startChapter(Chapter chapter) {
        tracker.start(chapter, permissions);

    }

    public Object togglePermission(List<Permission> permissions){
        return null;
    }
}
