package com.github.martinfrank.elitegames.aurealis;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.AdventureReader;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdventureLoader {

    private static final String SAMPLE_ADVENTURE = "/adventure/Verführung_zur_Entführung.xml";

    public static Adventure load() throws IOException {
        try (InputStream in = AdventureLoader.class.getResourceAsStream(SAMPLE_ADVENTURE)) {
            assertNotNull(in, SAMPLE_ADVENTURE + " not found on test classpath");
            return new AdventureReader().read(in);
        }
    }
}
