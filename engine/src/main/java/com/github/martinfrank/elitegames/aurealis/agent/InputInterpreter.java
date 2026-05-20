package com.github.martinfrank.elitegames.aurealis.agent;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.martinfrank.elitegames.aurealis.adventure.Item;
import com.github.martinfrank.elitegames.aurealis.adventure.Location;
import com.github.martinfrank.elitegames.aurealis.adventure.Person;
import com.github.martinfrank.elitegames.aurealis.game.ChatEntry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputInterpreter {

    private static final Logger LOG = LoggerFactory.getLogger(InputInterpreter.class);
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build();

    private final ChatLanguageModel model;

    public InputInterpreter(ChatLanguageModel model) {
        this.model = model;
    }

    public Intent interpret(String playerMessage, GameContext context) {
        String prompt = buildPrompt(playerMessage, context);
        LOG.debug("intent prompt:\n{}", prompt);
        try {
            String raw = model.generate(prompt);
            LOG.debug("intent llm raw: {}", raw);
            String json = extractJson(raw);
            if (json == null) {
                LOG.warn("no JSON in LLM response, falling back to FreeForm");
                return new Intent.FreeForm(playerMessage);
            }
            IntentDto dto = MAPPER.readValue(json, IntentDto.class);
            return IntentMapper.toIntent(dto, context, playerMessage);
        } catch (Exception e) {
            LOG.warn("intent extraction failed: {}", e.toString());
            return new Intent.FreeForm(playerMessage);
        }
    }

    private String buildPrompt(String playerMessage, GameContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("Du klassifizierst Spielereingaben in einem deutschsprachigen Pen-and-Paper-Rollenspiel.\n");
        sb.append("Wandle die Spielereingabe in eine strukturierte Intent um.\n\n");
        sb.append("Mögliche Intent-Typen (Feld 'kind'):\n");
        sb.append("- MoveTo: Spieler möchte den Ort wechseln. Pflichtfeld: locationId.\n");
        sb.append("- TalkTo: Spieler möchte eine anwesende Person ansprechen. Pflichtfeld: personId.\n");
        sb.append("- Inspect: Spieler untersucht etwas oder jemanden genauer. Pflichtfeld: targetId.\n");
        sb.append("- UseItem: Spieler benutzt einen Gegenstand. Pflichtfeld: itemId. Optional: targetId.\n");
        sb.append("- FreeForm: Eingabe passt in keine der obigen Kategorien. Pflichtfeld: text (Originaltext).\n\n");

        sb.append("Aktuelles Vokabular (gültige IDs):\n");
        sb.append("[Locations im aktuellen Chapter]\n");
        if (context.chapter() != null) {
            for (Location l : context.chapter().locations()) {
                sb.append("- ").append(l.id()).append(": ").append(l.name()).append('\n');
            }
        }
        sb.append("[Personen am aktuellen Ort]\n");
        for (Person p : context.presentPersons()) {
            sb.append("- ").append(p.id()).append(": ").append(p.name()).append('\n');
        }
        sb.append("[Items im aktuellen Chapter]\n");
        for (Item i : context.availableItems()) {
            sb.append("- ").append(i.id()).append(": ").append(i.name()).append('\n');
        }

        sb.append("\nBisheriger Chat-Verlauf:\n");
        for (ChatEntry e : context.recentHistory()) {
            sb.append('[').append(e.role()).append("] ").append(e.message()).append('\n');
        }

        sb.append("\nAktuelle Spielereingabe:\n");
        sb.append(playerMessage).append("\n\n");

        sb.append("Antworte AUSSCHLIESSLICH mit einem JSON-Objekt in diesem Schema:\n");
        sb.append("{\"kind\": \"MoveTo|TalkTo|Inspect|UseItem|FreeForm\", ");
        sb.append("\"locationId\": null, \"personId\": null, \"itemId\": null, \"targetId\": null, \"text\": null}\n");
        sb.append("Setze nur die Felder, die für die gewählte kind relevant sind. ");
        sb.append("Verwende ausschliesslich IDs aus dem oben gelisteten Vokabular. ");
        sb.append("Wenn du unsicher bist oder die Eingabe in keine Kategorie passt: kind=FreeForm und text=Originaltext.\n");
        return sb.toString();
    }

    private static String extractJson(String raw) {
        if (raw == null) return null;
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end <= start) return null;
        return raw.substring(start, end + 1);
    }
}
