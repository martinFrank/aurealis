package com.github.martinfrank.elitegames.aurealis.agent;

public record ActionResult(boolean accepted, String details) {

    public static ActionResult accepted(String details) {
        return new ActionResult(true, details);
    }

    public static ActionResult rejected(String details) {
        return new ActionResult(false, details);
    }
}
