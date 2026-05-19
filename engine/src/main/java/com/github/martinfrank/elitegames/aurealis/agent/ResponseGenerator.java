package com.github.martinfrank.elitegames.aurealis.agent;

import dev.langchain4j.model.chat.ChatLanguageModel;

public class ResponseGenerator {

    private final ChatLanguageModel model;

    public ResponseGenerator(ChatLanguageModel model) {
        this.model = model;
    }

    public String generate(GameContext context, Intent intent, ActionResult result) {
        String chapterName = context.chapter() != null ? context.chapter().name() : "?";
        String verdict = result.accepted() ? "ok" : "abgelehnt";
        return "[Spielleiter-Dummy | Chapter: " + chapterName
                + " | Intent: " + intent.getClass().getSimpleName()
                + " | " + verdict + "] " + result.details();
    }
}
