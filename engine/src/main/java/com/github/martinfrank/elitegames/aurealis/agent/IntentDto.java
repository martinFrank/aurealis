package com.github.martinfrank.elitegames.aurealis.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IntentDto(
        String kind,
        String locationId,
        String personId,
        String itemId,
        String targetId,
        String text
) {}
