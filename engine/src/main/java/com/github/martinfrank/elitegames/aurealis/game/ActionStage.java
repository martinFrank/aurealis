package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Location;
import com.github.martinfrank.elitegames.aurealis.agent.ActionResult;
import com.github.martinfrank.elitegames.aurealis.agent.Intent;

public class ActionStage {

    public ActionResult execute(Intent intent, Session session) {
        return switch (intent) {
            case Intent.MoveTo m -> executeMoveTo(m, session);
            case Intent.TalkTo t -> ActionResult.accepted("talk to " + t.personId());
            case Intent.Inspect i -> ActionResult.accepted("inspect " + i.targetId());
            case Intent.UseItem u -> ActionResult.accepted(
                    "use " + u.itemId() + (u.targetId() != null ? " on " + u.targetId() : ""));
            case Intent.FreeForm f -> ActionResult.accepted("free-form: " + f.text());
        };
    }

    private ActionResult executeMoveTo(Intent.MoveTo intent, Session session) {
        Chapter chapter = session.getCurrentChapter();
        if (chapter == null) {
            return ActionResult.rejected("Kein aktives Chapter.");
        }
        Location target = chapter.locations().stream()
                .filter(l -> intent.locationId().equals(l.id()))
                .findFirst()
                .orElse(null);
        if (target == null) {
            return ActionResult.rejected("Diese Location ist im aktuellen Chapter nicht verfügbar.");
        }
        if (!target.isReady(session.getTaskPredicates())) {
            return ActionResult.rejected("Du kommst da noch nicht hin — die Voraussetzungen sind nicht erfüllt.");
        }
        session.getParty().setLocation(target);
        return ActionResult.moved(target);
    }
}
