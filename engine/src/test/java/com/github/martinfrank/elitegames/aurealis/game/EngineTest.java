package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.AdventureLoader;
import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.AdventureReader;
import com.github.martinfrank.elitegames.aurealis.party.Party;
import dev.langchain4j.agent.tool.P;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EngineTest {

    private static final String SAMPLE_ADVENTURE = "/adventure/Verführung_zur_Entführung.xml";

    @Test
    void startChapter() throws IOException {
        Adventure adventure = AdventureLoader.load();
        assertEquals("Verführung zur Entführung", adventure.title());

        Engine engine = new Engine(adventure, new Party());
    }

}