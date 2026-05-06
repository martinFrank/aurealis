package com.github.martinfrank.elitegames.aurealis.adventure;

public record Permission(String id, String name, String description, State state) {

    public enum State {
        GRANTED, DENIED
    }

}
