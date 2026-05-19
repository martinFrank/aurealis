package com.github.martinfrank.elitegames.aurealis.adventure;

import com.github.martinfrank.elitegames.aurealis.game.TaskPredicates;

import java.util.List;

public record Task(
        String id,
        String name,
        String description, //what
        String purpose, //why
        boolean required,
        List<TaskPredicate> requiredTaskPredicates,
        List<TaskPredicate> grantedTaskPredicates,
        CutScene startCutScene,
        CutScene endCutScene) {

    public enum State {
        NOT_READY, IN_PROGRESS, COMPLETED, FAILED
    }

    public State getState(TaskPredicates taskPredicates) {
        for(TaskPredicate required: requiredTaskPredicates){
            if (!taskPredicates.isGranted(required)) { //falls eine fehlt
                return State.NOT_READY;
            }
        }
        return State.IN_PROGRESS; //sind alle da, ist es
    }
}
