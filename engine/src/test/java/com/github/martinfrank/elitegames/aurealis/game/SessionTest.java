package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.AdventureLoader;
import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Permission;
import com.github.martinfrank.elitegames.aurealis.party.Party;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void testSession() throws IOException {
        Adventure adventure = AdventureLoader.load();
        Session session = new Session(adventure, new Party());

        session.init();


        Permissions permissions = session.getPermissions();
        Permission permission = permissions.getByName("SUCHE_NACH_DEM_VATER");
        Assertions.assertNotNull(permission);


        PermissionUpdateResult result = session.grant(permission);

        int i = 0;

    }
}