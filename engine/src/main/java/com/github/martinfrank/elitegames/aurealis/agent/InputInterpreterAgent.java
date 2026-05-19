package com.github.martinfrank.elitegames.aurealis.agent;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;

import java.util.List;
import java.util.Objects;

public class InputInterpreterAgent {

    private final ChatLanguageModel model;

    public InputInterpreterAgent(ChatLanguageModel model) {
        this.model = model;
    }


}
