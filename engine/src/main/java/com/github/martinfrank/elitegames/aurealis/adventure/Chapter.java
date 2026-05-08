package com.github.martinfrank.elitegames.aurealis.adventure;

import com.github.martinfrank.elitegames.aurealis.game.Permissions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Chapter (
        String id,
        int position,
        String name,
        String description,
        String aiHints,
        Location startLocation,
        String startTime,
        List<Location> locations,
        List<Item> items,
        List<Task> tasks,
        List<Permission> requiredPermissions,
        CutScene startCutScene,
        CutScene endCutScene)
        implements Comparable<Chapter> {

    @Override
    public int compareTo(@NotNull Chapter c) {
        return Integer.compare(position, c.position());
    }

    public enum State {
        NOT_READY, OPEN, IN_PROGRESS, DONE;
    }

    public boolean isReady(Permissions permissions) {
        for(Permission required: requiredPermissions){
            if (!permissions.isGranted(required)) { //falls eine fehlt
                return false;
            }
        }
        return true;
    }
}
