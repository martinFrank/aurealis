package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.AdventureLoader;
import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.adventure.Permission;
import com.github.martinfrank.elitegames.aurealis.adventure.Task;
import com.github.martinfrank.elitegames.aurealis.party.Party;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void testSession() throws IOException {
        Adventure adventure = AdventureLoader.load();
        Session session = new Session(adventure, new Party());

        session.init();

        Chapter currentChapter = session.getCurrentChapter();
        System.out.println("current chapter: "+currentChapter.name());

        List<Task> currentTasks = session.getCurrentTasks();
        String tasksString = currentTasks.stream().map(Task::name).collect(Collectors.joining(", "));
        System.out.println("current tasks: "+tasksString);

        Task task = currentTasks.getFirst();

        System.out.println("complete task: "+task.name());
        session.completeTask(task);
        List<Permission> grantedPermissions = task.grantedPermissions();

        for(Permission permission : grantedPermissions) {
            PermissionUpdateResult result = session.grant(permission);
            if(result.haveTasksChanged()){
                for(TaskChange changedTask: result.taskChanges) {
                    System.out.println("changed tasks: "+changedTask.task().name()+" from "+changedTask.oldState()+ " -> "+changedTask.newState());
                }
            }
        }

        int i = 0;

    }
}