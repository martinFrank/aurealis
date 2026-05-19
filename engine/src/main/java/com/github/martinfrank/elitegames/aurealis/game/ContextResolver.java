package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Item;
import com.github.martinfrank.elitegames.aurealis.adventure.Location;
import com.github.martinfrank.elitegames.aurealis.adventure.Person;
import com.github.martinfrank.elitegames.aurealis.agent.GameContext;

import java.util.List;

public class ContextResolver {

    private static final int HISTORY_WINDOW = 10;

    public GameContext resolve(Session session) {
        Chapter chapter = session.getCurrentChapter();
        Location currentLocation = session.getParty().getLocation();
        List<Person> presentPersons = currentLocation != null
                ? currentLocation.persons().stream().map(Location.LocalizedPerson::person).toList()
                : List.of();
        List<Item> availableItems = chapter != null ? chapter.items() : List.of();
        return new GameContext(
                chapter,
                currentLocation,
                presentPersons,
                availableItems,
                session.getCurrentTasks(),
                session.chat.recentHistory(HISTORY_WINDOW)
        );
    }
}
