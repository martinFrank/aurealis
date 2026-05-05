package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.party.Party;

public class Engine {

    private final Session session;

    public Engine(Adventure adventure, Party party) {
        session = new Session(adventure, party);
    }

    public void start(){
        session.init();
        startChapter();
    }

    public void handleTurn(String playerMessage){

    }

    private void startChapter() {
        //intro
    }
}
