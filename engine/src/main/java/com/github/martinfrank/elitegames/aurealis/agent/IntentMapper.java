package com.github.martinfrank.elitegames.aurealis.agent;

import com.github.martinfrank.elitegames.aurealis.adventure.Item;
import com.github.martinfrank.elitegames.aurealis.adventure.Location;
import com.github.martinfrank.elitegames.aurealis.adventure.Person;

public class IntentMapper {

    public static Intent toIntent(IntentDto dto, GameContext context, String fallbackText) {
        if (dto == null || isBlank(dto.kind())) return new Intent.FreeForm(fallbackText);
        return switch (dto.kind()) {
            case "MoveTo" -> mapMoveTo(dto, context, fallbackText);
            case "TalkTo" -> mapTalkTo(dto, context, fallbackText);
            case "Inspect" -> mapInspect(dto, context, fallbackText);
            case "UseItem" -> mapUseItem(dto, context, fallbackText);
            case "FreeForm" -> new Intent.FreeForm(isBlank(dto.text()) ? fallbackText : dto.text());
            default -> new Intent.FreeForm(fallbackText);
        };
    }

    private static Intent mapMoveTo(IntentDto dto, GameContext ctx, String fallback) {
        if (isBlank(dto.locationId())) return new Intent.FreeForm(fallback);
        if (ctx.chapter() == null) return new Intent.FreeForm(fallback);
        boolean known = ctx.chapter().locations().stream()
                .anyMatch(l -> dto.locationId().equals(l.id()));
        return known ? new Intent.MoveTo(dto.locationId()) : new Intent.FreeForm(fallback);
    }

    private static Intent mapTalkTo(IntentDto dto, GameContext ctx, String fallback) {
        if (isBlank(dto.personId())) return new Intent.FreeForm(fallback);
        boolean known = ctx.presentPersons().stream()
                .anyMatch(p -> dto.personId().equals(p.id()));
        return known ? new Intent.TalkTo(dto.personId()) : new Intent.FreeForm(fallback);
    }

    private static Intent mapInspect(IntentDto dto, GameContext ctx, String fallback) {
        if (isBlank(dto.targetId())) return new Intent.FreeForm(fallback);
        return isKnownTarget(dto.targetId(), ctx)
                ? new Intent.Inspect(dto.targetId())
                : new Intent.FreeForm(fallback);
    }

    private static Intent mapUseItem(IntentDto dto, GameContext ctx, String fallback) {
        if (isBlank(dto.itemId())) return new Intent.FreeForm(fallback);
        boolean validItem = ctx.availableItems().stream()
                .anyMatch(i -> dto.itemId().equals(i.id()));
        if (!validItem) return new Intent.FreeForm(fallback);
        String target = dto.targetId();
        if (!isBlank(target) && !isKnownTarget(target, ctx)) return new Intent.FreeForm(fallback);
        return new Intent.UseItem(dto.itemId(), target);
    }

    private static boolean isKnownTarget(String id, GameContext ctx) {
        if (ctx.currentLocation() != null && id.equals(ctx.currentLocation().id())) return true;
        for (Person p : ctx.presentPersons()) if (id.equals(p.id())) return true;
        for (Item i : ctx.availableItems()) if (id.equals(i.id())) return true;
        if (ctx.chapter() != null) {
            for (Location l : ctx.chapter().locations()) if (id.equals(l.id())) return true;
        }
        return false;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
