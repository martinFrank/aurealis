package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Permission;

import java.util.ArrayList;
import java.util.List;

public class Progress {

    private List<Permission> permissions;

    public Progress(Adventure adventure) {
        permissions = new ArrayList<>(adventure.permissions());
    }

}
