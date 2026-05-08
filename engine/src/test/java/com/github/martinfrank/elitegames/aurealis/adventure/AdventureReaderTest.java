package com.github.martinfrank.elitegames.aurealis.adventure;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdventureReaderTest {

    private final AdventureReader reader = new AdventureReader();

    @Test
    void readsAdventureMetadata() throws IOException {
        Adventure adventure = readSample();

        assertEquals("The Lost Beacon", adventure.title());
        assertEquals("A short test adventure.", adventure.description());
        assertEquals("Martin", adventure.author());
    }

    @Test
    void readsAdventureLevelEntities() throws IOException {
        Adventure adventure = readSample();

        assertEquals(2, adventure.locations().size());
        assertEquals(2, adventure.persons().size());
        assertEquals(1, adventure.items().size());
        assertEquals(2, adventure.permissions().size());
        assertEquals(2, adventure.cutScenes().size());
        assertEquals(1, adventure.chapters().size());
    }

    @Test
    void readsLocationFields() throws IOException {
        Adventure adventure = readSample();
        Location bridge = findById(adventure.locations(), "loc.bridge", Location::id);

        assertEquals("Bridge", bridge.name());
        assertEquals("The ship's bridge.", bridge.description());
        assertEquals("Center of command.", bridge.aiHints());
    }

    @Test
    void locationRequiredPermissionsAreSameInstancesAsAdventurePermissions() throws IOException {
        Adventure adventure = readSample();
        Location bridge = findById(adventure.locations(), "loc.bridge", Location::id);
        Permission bridgeAccess = findById(adventure.permissions(), "prm.bridgeAccess", Permission::id);

        assertEquals(1, bridge.requiredPermissions().size());
        assertSame(bridgeAccess, bridge.requiredPermissions().get(0));
    }

    @Test
    void locationLocalizedPersonsResolveBothRefs() throws IOException {
        Adventure adventure = readSample();
        Location bridge = findById(adventure.locations(), "loc.bridge", Location::id);
        Person captain = findById(adventure.persons(), "per.captain", Person::id);
        Permission bridgeAccess = findById(adventure.permissions(), "prm.bridgeAccess", Permission::id);

        assertEquals(1, bridge.persons().size());
        Location.LocalizedPerson lp = bridge.persons().get(0);
        assertSame(captain, lp.person());
        assertSame(bridgeAccess, lp.requiredPermission());
    }

    @Test
    void localizedPersonWithoutPermissionIsAccessible() throws IOException {
        Adventure adventure = readSample();
        Location cargo = findById(adventure.locations(), "loc.cargo", Location::id);
        Person engineer = findById(adventure.persons(), "per.engineer", Person::id);

        assertEquals(1, cargo.persons().size());
        Location.LocalizedPerson lp = cargo.persons().get(0);
        assertSame(engineer, lp.person());
        assertEquals(null, lp.requiredPermission());
    }

    @Test
    void readsPersonFields() throws IOException {
        Adventure adventure = readSample();
        Person captain = findById(adventure.persons(), "per.captain", Person::id);

        assertEquals("Captain Ada", captain.name());
        assertEquals("Tall, gray uniform.", captain.appearance());
        assertEquals("Calm, decisive, slow to anger.", captain.personality());
        assertEquals("Captain", captain.role());
        assertEquals("Strict but fair.", captain.aiHints());
    }

    @Test
    void readsItemFields() throws IOException {
        Adventure adventure = readSample();
        Item beacon = findById(adventure.items(), "itm.beacon", Item::id);

        assertEquals("Beacon", beacon.name());
        assertEquals("Small metallic device.", beacon.appearance());
        assertEquals("Sends a distress signal.", beacon.purpose());
        assertEquals("Plot critical.", beacon.aiHints());
    }

    @Test
    void chapterReferencesShareInstancesWithAdventureLevel() throws IOException {
        Adventure adventure = readSample();
        Chapter chapter = adventure.chapters().get(0);

        Location bridge = findById(adventure.locations(), "loc.bridge", Location::id);
        Location cargo = findById(adventure.locations(), "loc.cargo", Location::id);
        Item beacon = findById(adventure.items(), "itm.beacon", Item::id);

        assertSame(bridge, chapter.startLocation());
        assertSame(bridge, chapter.locations().get(0));
        assertSame(cargo, chapter.locations().get(1));
        assertSame(beacon, chapter.items().get(0));
    }

    @Test
    void chapterRequiredPermissionsAreSameInstancesAsAdventurePermissions() throws IOException {
        Adventure adventure = readSample();
        Chapter chapter = adventure.chapters().get(0);
        Permission bridgeAccess = findById(adventure.permissions(), "prm.bridgeAccess", Permission::id);

        assertEquals(1, chapter.requiredPermissions().size());
        assertSame(bridgeAccess, chapter.requiredPermissions().get(0));
    }

    @Test
    void taskPermissionsAreSameInstancesAsAdventurePermissions() throws IOException {
        Adventure adventure = readSample();
        Task task = adventure.chapters().get(0).tasks().get(0);

        Permission bridgeAccess = findById(adventure.permissions(), "prm.bridgeAccess", Permission::id);
        Permission beaconActivated = findById(adventure.permissions(), "prm.beaconActivated", Permission::id);

        assertSame(bridgeAccess, task.requiredPermissions().get(0));
        assertSame(beaconActivated, task.grantedPermissions().get(0));
        assertTrue(task.required());
    }

    @Test
    void readsTaskFields() throws IOException {
        Adventure adventure = readSample();
        Task task = adventure.chapters().get(0).tasks().get(0);

        assertEquals("Activate Beacon", task.name());
        assertEquals("Activate the beacon.", task.description());
        assertEquals("Call for help.", task.purpose());
    }

    @Test
    void chapterAndTaskCutScenesAreSameInstancesAsAdventureCutScenes() throws IOException {
        Adventure adventure = readSample();
        Chapter chapter = adventure.chapters().get(0);
        Task task = chapter.tasks().get(0);

        CutScene intro = findById(adventure.cutScenes(), "cs.intro", CutScene::id);
        CutScene beaconLit = findById(adventure.cutScenes(), "cs.beaconLit", CutScene::id);

        assertSame(intro, chapter.startCutScene());
        assertEquals(null, chapter.endCutScene());
        assertEquals(null, task.startCutScene());
        assertSame(beaconLit, task.endCutScene());
    }

    @Test
    void rejectsDanglingLocationReference() {
        String badXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <adventure xmlns="https://github.com/martinfrank/elitegames/aurealis/adventure">
                    <title>Broken</title>
                    <description>x</description>
                    <author>x</author>
                    <locations>
                        <location id="loc.a"><name>A</name><description>x</description><aiHints>x</aiHints></location>
                    </locations>
                    <persons/>
                    <items/>
                    <permissions/>
                    <cutScenes/>
                    <chapters>
                        <chapter id="ch.1" position="1">
                            <name>x</name><description>x</description><aiHints>x</aiHints>
                            <startTime>x</startTime>
                            <startLocationRef ref="loc.does-not-exist"/>
                            <locations/><persons/><items/><tasks/><requiredPermissions/>
                        </chapter>
                    </chapters>
                </adventure>
                """;

        IOException ex = assertThrows(IOException.class,
                () -> reader.read(new ByteArrayInputStream(badXml.getBytes(StandardCharsets.UTF_8))));
        assertNotNull(ex.getCause());
    }

    private Adventure readSample() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/adventure/sample-adventure.xml")) {
            assertNotNull(in, "sample-adventure.xml not found on test classpath");
            return reader.read(in);
        }
    }

    private static <T> T findById(List<T> list, String id, Function<T, String> idFn) {
        return list.stream()
                .filter(e -> id.equals(idFn.apply(e)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Entity with id '" + id + "' not found"));
    }
}
