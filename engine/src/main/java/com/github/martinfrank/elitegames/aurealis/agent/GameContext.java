package com.github.martinfrank.elitegames.aurealis.agent;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Item;
import com.github.martinfrank.elitegames.aurealis.adventure.Location;
import com.github.martinfrank.elitegames.aurealis.adventure.Person;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;
import com.github.martinfrank.elitegames.aurealis.game.ChatEntry;

import java.util.List;

public record GameContext(
        Chapter chapter,
        Location currentLocation,
        List<Person> presentPersons,
        List<Item> availableItems,
        List<Task> currentTasks,
        List<ChatEntry> recentHistory
) {}
