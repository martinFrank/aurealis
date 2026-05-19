package com.github.martinfrank.elitegames.aurealis.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private static final Logger LOG = LoggerFactory.getLogger(Chat.class);

    public enum Role {
        CUT_SCENE, PLAYER, GAME_MASTER
    }

    private final List<ChatEntry> history = new ArrayList<>();

    public void add(Role role, String message){
        String trim = message.replace("\t", "");
        history.add(new ChatEntry(role, trim));
        LOG.info(trim);
    }

    public List<ChatEntry> recentHistory(int max) {
        int start = Math.max(0, history.size() - max);
        return List.copyOf(history.subList(start, history.size()));
    }

}
