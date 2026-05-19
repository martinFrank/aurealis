package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.AdventureLoader;
import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.party.Party;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineTest {

    static void main(String[] args) throws IOException {
        Adventure adventure = AdventureLoader.load();
        assertEquals("Verführung zur Entführung", adventure.title());

        Engine engine = new Engine(adventure, new Party());
        engine.start();
    }
}