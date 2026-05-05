package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Location;
import com.github.martinfrank.elitegames.aurealis.adventure.Permission;
import com.github.martinfrank.elitegames.aurealis.party.Party;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private final Adventure adventure;
    private final Party party;

    private Location partyLocation;
    private String time;
    private List<Permission> permissions;
    private Chapter chapter;

    public Session(Adventure adventure, Party party) {
        this.adventure = adventure;
        adventure.chapters().sort(null);
        this.party = party;
        permissions = new ArrayList<>(adventure.permissions());
    }


    public void init() {
        this.chapter = adventure.chapters().getFirst();
        partyLocation = chapter.startLocation();
        time = chapter.startTime();
    }
}
