package com.github.martinfrank.elitegames.aurealis.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;
import com.github.martinfrank.elitegames.aurealis.game.ChatEntry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class TaskFulfillmentJudge {

    private static final Logger LOG = LoggerFactory.getLogger(TaskFulfillmentJudge.class);
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .build();
    private static final int HISTORY_FOR_JUDGE = 5;

    private final ChatLanguageModel model;

    public TaskFulfillmentJudge(ChatLanguageModel model) {
        this.model = model;
    }

    public List<Task> evaluate(GameContext context, Intent intent, ActionResult result, List<Task> inProgressTasks) {
        if (inProgressTasks.isEmpty()) return List.of();

        String prompt = buildPrompt(context, intent, result, inProgressTasks);
        LOG.trace("fulfillment prompt:\n{}", prompt);
        try {
            String raw = model.generate(prompt);
            LOG.trace("fulfillment llm raw: {}", raw);
            String json = extractJson(raw);
            if (json == null) {
                LOG.warn("no JSON in judge response, treating as no fulfillment");
                return List.of();
            }
            FulfillmentDto dto = MAPPER.readValue(json, FulfillmentDto.class);
            if (dto.fulfilledTaskIds() == null || dto.fulfilledTaskIds().isEmpty()) return List.of();
            Set<String> fulfilledIds = Set.copyOf(dto.fulfilledTaskIds());
            return inProgressTasks.stream()
                    .filter(t -> fulfilledIds.contains(t.id()))
                    .toList();
        } catch (Exception e) {
            LOG.warn("fulfillment evaluation failed: {}", e.toString());
            return List.of();
        }
    }

    private String buildPrompt(GameContext context, Intent intent, ActionResult result, List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("Du bewertest in einem deutschsprachigen Pen-and-Paper-Rollenspiel, ");
        sb.append("welche der laufenden Tasks durch den letzten Spielerzug erfüllt wurden.\n\n");

        sb.append("Laufende Tasks:\n");
        for (Task t : tasks) {
            sb.append("- ").append(t.id()).append(": \"").append(t.name()).append("\"\n");
            sb.append("  Description (was): ").append(t.description()).append('\n');
            sb.append("  Purpose (warum): ").append(t.purpose()).append('\n');
        }

        sb.append("\nLetzter Spielerzug:\n");
        sb.append("- Intent: ").append(intent.getClass().getSimpleName()).append(' ').append(intent).append('\n');
        sb.append("- Resultat: ").append(result.accepted() ? "akzeptiert" : "abgelehnt").append('\n');
        if (result.details() != null) sb.append("- Details: ").append(result.details()).append('\n');

        sb.append("\nLetzte Chat-Einträge:\n");
        List<ChatEntry> history = context.recentHistory();
        int from = Math.max(0, history.size() - HISTORY_FOR_JUDGE);
        for (int i = from; i < history.size(); i++) {
            ChatEntry e = history.get(i);
            sb.append('[').append(e.role()).append("] ").append(e.message()).append('\n');
        }

        sb.append("\nAntworte AUSSCHLIESSLICH mit einem JSON-Objekt in diesem Schema:\n");
        sb.append("{\"fulfilledTaskIds\": [\"tsk.xxx\", ...]}\n");
        sb.append("Liste nur die IDs der Tasks, die durch den letzten Zug klar erfüllt sind. ");
        sb.append("Wenn unsicher oder keine erfüllt: leere Liste.\n");
        return sb.toString();
    }

    private static String extractJson(String raw) {
        if (raw == null) return null;
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end <= start) return null;
        return raw.substring(start, end + 1);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FulfillmentDto(List<String> fulfilledTaskIds) {}
}
