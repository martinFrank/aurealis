package com.github.martinfrank.elitegames.aurealis.agent;

import com.github.martinfrank.elitegames.aurealis.adventure.Item;
import com.github.martinfrank.elitegames.aurealis.adventure.Person;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;
import com.github.martinfrank.elitegames.aurealis.adventure.TaskPredicate;
import com.github.martinfrank.elitegames.aurealis.game.ChatEntry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseGenerator.class);

    private final ChatLanguageModel model;

    public ResponseGenerator(ChatLanguageModel model) {
        this.model = model;
    }

    public String generate(GameContext context, String playerMessage, Intent intent, ActionResult result) {
        String prompt = buildPrompt(context, playerMessage, intent, result);
        LOG.trace("response prompt:\n{}", prompt);
        try {
            String narrative = model.generate(prompt);
            LOG.trace("response llm raw: {}", narrative);
            if (narrative == null || narrative.isBlank()) return fallback(result);
            return narrative.strip();
        } catch (Exception e) {
            LOG.warn("response generation failed: {}", e.toString());
            return fallback(result);
        }
    }

    private String fallback(ActionResult result) {
        if (!result.accepted()) return "Das gelingt dir gerade nicht.";
        if (result.movedTo() != null) return "Du begibst dich zu " + result.movedTo().name() + ".";
        return "Du tust, was du vorhattest.";
    }

    private String buildPrompt(GameContext context, String playerMessage, Intent intent, ActionResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Du bist der Spielleiter eines deutschsprachigen Pen-and-Paper-Rollenspiels im DSA/Aventurien-Stil.\n");
        sb.append("Beschreibe in 2. Person, narrativ und atmosphärisch, was die Heldengruppe erlebt.\n");
        sb.append("Bleibe in der Rolle, nutze keine technischen Begriffe (z. B. \"Intent\", \"Task\", \"Predicate\") und ");
        sb.append("erwähne KEINE technischen IDs.\n\n");

        if (context.chapter() != null) {
            sb.append("[Chapter] ").append(context.chapter().name()).append('\n');
            appendIfNotBlank(sb, "  Beschreibung: ", context.chapter().description());
            appendIfNotBlank(sb, "  Spielleiter-Hinweise: ", context.chapter().aiHints());
        }

        if (context.currentLocation() != null) {
            sb.append("[Aktueller Ort] ").append(context.currentLocation().name()).append('\n');
            appendIfNotBlank(sb, "  Beschreibung: ", context.currentLocation().description());
            appendIfNotBlank(sb, "  Spielleiter-Hinweise: ", context.currentLocation().aiHints());
        }

        if (!context.presentPersons().isEmpty()) {
            sb.append("[Anwesende Personen]\n");
            for (Person p : context.presentPersons()) {
                sb.append("- ").append(p.name());
                if (p.role() != null && !p.role().isBlank()) sb.append(" (").append(p.role()).append(')');
                sb.append('\n');
                appendIfNotBlank(sb, "  Erscheinung: ", p.appearance());
                appendIfNotBlank(sb, "  Persönlichkeit: ", p.personality());
                appendIfNotBlank(sb, "  Spielleiter-Hinweise: ", p.aiHints());
            }
        }

        if (!context.availableItems().isEmpty()) {
            sb.append("[Verfügbare Items]\n");
            for (Item it : context.availableItems()) {
                sb.append("- ").append(it.name()).append('\n');
                appendIfNotBlank(sb, "  Erscheinung: ", it.appearance());
                appendIfNotBlank(sb, "  Zweck: ", it.purpose());
                appendIfNotBlank(sb, "  Spielleiter-Hinweise: ", it.aiHints());
            }
        }

        if (!context.currentTasks().isEmpty()) {
            sb.append("[Hintergrund-Ziele — NICHT direkt erwähnen, nur indirekt durchscheinen lassen]\n");
            for (Task t : context.currentTasks()) {
                sb.append("- ").append(t.name()).append(": ").append(t.description()).append('\n');
            }
        }

        if (!context.recentHistory().isEmpty()) {
            sb.append("\n[Bisheriger Chat-Verlauf]\n");
            for (ChatEntry e : context.recentHistory()) {
                sb.append('[').append(e.role()).append("] ").append(e.message()).append('\n');
            }
        }

        sb.append("\n[Aktuelle Spielereingabe]\n").append(playerMessage).append('\n');

        sb.append("\n[Vom System bestimmtes Resultat — beschreibe es in deiner Erzählung]\n");
        sb.append("- Aktion: ").append(intent.getClass().getSimpleName()).append('\n');
        sb.append("- Status: ").append(result.accepted() ? "erlaubt" : "abgelehnt").append('\n');
        if (result.movedTo() != null) {
            sb.append("- Die Gruppe ist jetzt am Ort: ").append(result.movedTo().name()).append('\n');
        }
        if (!result.completedTasks().isEmpty()) {
            sb.append("- Erfüllt: ");
            for (Task t : result.completedTasks()) sb.append(t.name()).append("; ");
            sb.append('\n');
        }
        if (!result.grantedPredicates().isEmpty()) {
            sb.append("- Neue Erkenntnisse / Zustände: ");
            for (TaskPredicate p : result.grantedPredicates()) sb.append(p.name()).append("; ");
            sb.append('\n');
        }
        if (result.newChapter() != null) {
            sb.append("- Übergang in das nächste Kapitel: ").append(result.newChapter().name()).append('\n');
        }

        sb.append("\n[Anweisungen]\n");
        sb.append("Antworte auf Deutsch, 2-4 Sätze, in 2. Person Plural (\"ihr\") oder Singular je nach Stil.\n");
        sb.append("Bei erlaubten Aktionen: schildere atmosphärisch, was passiert.\n");
        sb.append("Bei abgelehnten Aktionen: erkläre in-character, warum es nicht gelingt.\n");
        sb.append("Erwähne nur Personen/Orte/Items aus dem oben gelisteten Kontext. ");
        sb.append("Im Rahmen der Spielleiter-Hinweise darfst du atmosphärisch ausschmücken oder Statisten erfinden.\n");
        sb.append("Erwähne keine IDs, keine technischen Begriffe und keine Hintergrund-Ziele wörtlich.\n");
        return sb.toString();
    }

    private static void appendIfNotBlank(StringBuilder sb, String label, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(label).append(value.strip()).append('\n');
        }
    }
}
