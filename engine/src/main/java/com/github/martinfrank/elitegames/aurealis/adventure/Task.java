package com.github.martinfrank.elitegames.aurealis.adventure;

import com.github.martinfrank.elitegames.aurealis.game.Permissions;

import java.util.List;

public record Task(
        String id,
        String name,
        String description, //what
        String purpose, //why
        boolean required,
        List<Permission> requiredPermissions,
        List<Permission> grantedPermissions,
        CutScene startCutScene,
        CutScene endCutScene) {

    public enum State {
        NOT_READY, IN_PROGRESS, COMPLETED, FAILED
    }

    public State getState(Permissions permissions) {
        for(Permission required: requiredPermissions){
            Permission permission = permissions.getById(required.id());
            if (permission == null || permission.state() == Permission.State.DENIED ){ //falls eine fehlt
                return State.NOT_READY;
            }
        }
        return State.IN_PROGRESS; //sind alle da, ist es
    }
}
