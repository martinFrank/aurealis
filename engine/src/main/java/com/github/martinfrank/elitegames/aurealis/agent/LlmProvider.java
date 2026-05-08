package com.github.martinfrank.elitegames.aurealis.agent;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;

public class LlmProvider {


    private static final String OLLAMA_URL = "http://192.168.0.251:11434";
    //    private static final String MODEL = "qwen3:30b";
//    private static final String MODEL = "qwen2.5:7b"; //gut
    private static final String MODEL = "llama3.1:8b"; //gut
//    private static final String MODEL = "qwen3:32b"; //von cahtgpt empfohlen - SPILL


    public static ChatLanguageModel defaultChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(OLLAMA_URL)
                .modelName(MODEL)
                .timeout(Duration.ofMinutes(5))
                .build();
    }

}
