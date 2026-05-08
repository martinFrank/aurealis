package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Task;

public record TaskChange(Task task, Task.State oldState, Task.State newState) {
}
