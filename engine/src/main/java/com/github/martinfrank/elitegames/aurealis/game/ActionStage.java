package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.agent.ActionResult;
import com.github.martinfrank.elitegames.aurealis.agent.Intent;

public class ActionStage {

    public ActionResult execute(Intent intent, Session session) {
        return switch (intent) {
            case Intent.MoveTo m -> ActionResult.accepted("move to " + m.locationId());
            case Intent.TalkTo t -> ActionResult.accepted("talk to " + t.personId());
            case Intent.Inspect i -> ActionResult.accepted("inspect " + i.targetId());
            case Intent.UseItem u -> ActionResult.accepted("use " + u.itemId() + " on " + u.targetId());
            case Intent.FreeForm f -> ActionResult.accepted("free-form: " + f.text());
        };
    }
}
