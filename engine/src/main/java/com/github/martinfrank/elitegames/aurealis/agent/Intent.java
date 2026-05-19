package com.github.martinfrank.elitegames.aurealis.agent;

public sealed interface Intent {
    record MoveTo(String locationId) implements Intent {}
    record TalkTo(String personId) implements Intent {}
    record Inspect(String targetId) implements Intent {}
    record UseItem(String itemId, String targetId) implements Intent {}
    record FreeForm(String text) implements Intent {}
}
