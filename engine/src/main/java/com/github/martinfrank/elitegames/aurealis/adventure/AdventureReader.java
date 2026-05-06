package com.github.martinfrank.elitegames.aurealis.adventure;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdventureReader {

    private static final String NS = "https://github.com/martinfrank/elitegames/aurealis/adventure";
    private static final String SCHEMA_RESOURCE = "/schema/adventure.xsd";

    public Adventure read(Path xmlPath) throws IOException {
        try (InputStream in = Files.newInputStream(xmlPath)) {
            return read(in);
        }
    }

    public Adventure read(InputStream xmlInputStream) throws IOException {
        Document doc = parse(xmlInputStream);
        Element root = doc.getDocumentElement();

        Map<String, Permission> permissions = readPermissions(root);
        Map<String, CutScene> cutScenes = readCutScenes(root);
        Map<String, Person> persons = readPersons(root);
        Map<String, Item> items = readItems(root);
        Map<String, Location> locations = readLocations(root, persons, permissions);
        List<Chapter> chapters = readChapters(root, locations, items, permissions, cutScenes);

        return new Adventure(
                textOf(root, "title"),
                textOf(root, "description"),
                textOf(root, "author"),
                chapters,
                new ArrayList<>(permissions.values()),
                new ArrayList<>(persons.values()),
                new ArrayList<>(items.values()),
                new ArrayList<>(locations.values()),
                new ArrayList<>(cutScenes.values())
        );
    }

    private Document parse(InputStream in) throws IOException {
        try {
            URL schemaUrl = getClass().getResource(SCHEMA_RESOURCE);
            if (schemaUrl == null) {
                throw new IOException("Schema not found on classpath: " + SCHEMA_RESOURCE);
            }
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaUrl);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setSchema(schema);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ThrowingErrorHandler());
            return builder.parse(in);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to parse adventure XML", e);
        }
    }

    private Map<String, Location> readLocations(
            Element root,
            Map<String, Person> persons,
            Map<String, Permission> permissions
    ) {
        Map<String, Location> map = new LinkedHashMap<>();
        Element container = childElement(root, "locations");
        if (container == null) return map;
        for (Element e : children(container, "location")) {
            String id = e.getAttribute("id");
            map.put(id, new Location(
                    id,
                    textOf(e, "name"),
                    textOf(e, "description"),
                    textOf(e, "aiHints"),
                    readRefs(e, "requiredPermissions", "permissionRef", permissions, "permission"),
                    readLocalizedPersons(e, persons, permissions)
            ));
        }
        return map;
    }

    private List<Location.LocalizedPerson> readLocalizedPersons(
            Element locationEl,
            Map<String, Person> persons,
            Map<String, Permission> permissions
    ) {
        List<Location.LocalizedPerson> result = new ArrayList<>();
        Element container = childElement(locationEl, "persons");
        if (container == null) return result;
        for (Element lpEl : children(container, "localizedPerson")) {
            Element personRef = childElement(lpEl, "personRef");
            Element permRef = childElement(lpEl, "permissionRef");
            Person person = resolve(persons, personRef.getAttribute("ref"), "person");
            Permission permission = resolve(permissions, permRef.getAttribute("ref"), "permission");
            result.add(new Location.LocalizedPerson(person, permission));
        }
        return result;
    }

    private Map<String, Person> readPersons(Element root) {
        Map<String, Person> map = new LinkedHashMap<>();
        Element container = childElement(root, "persons");
        if (container == null) return map;
        for (Element e : children(container, "person")) {
            String id = e.getAttribute("id");
            map.put(id, new Person(
                    id,
                    textOf(e, "name"),
                    textOf(e, "appearance"),
                    textOf(e, "personality"),
                    textOf(e, "role"),
                    textOf(e, "aiHints")
            ));
        }
        return map;
    }

    private Map<String, Item> readItems(Element root) {
        Map<String, Item> map = new LinkedHashMap<>();
        Element container = childElement(root, "items");
        if (container == null) return map;
        for (Element e : children(container, "item")) {
            String id = e.getAttribute("id");
            map.put(id, new Item(
                    id,
                    textOf(e, "name"),
                    textOf(e, "appearance"),
                    textOf(e, "purpose"),
                    textOf(e, "aiHints")
            ));
        }
        return map;
    }

    private Map<String, Permission> readPermissions(Element root) {
        Map<String, Permission> map = new LinkedHashMap<>();
        Element container = childElement(root, "permissions");
        if (container == null) return map;
        for (Element e : children(container, "permission")) {
            String id = e.getAttribute("id");
            map.put(id, new Permission(
                    id,
                    textOf(e, "name"),
                    textOf(e, "description"),
                    textOf(e, "grantCondition"),
                    Permission.State.valueOf(textOf(e, "state"))
            ));
        }
        return map;
    }

    private Map<String, CutScene> readCutScenes(Element root) {
        Map<String, CutScene> map = new LinkedHashMap<>();
        Element container = childElement(root, "cutScenes");
        if (container == null) return map;
        for (Element e : children(container, "cutScene")) {
            String id = e.getAttribute("id");
            map.put(id, new CutScene(id, textOf(e, "name"), textOf(e, "text")));
        }
        return map;
    }

    private CutScene readOptionalCutSceneRef(
            Element parent,
            String localName,
            Map<String, CutScene> cutScenes
    ) {
        Element refEl = childElement(parent, localName);
        if (refEl == null) return null;
        String id = refEl.getAttribute("ref");
        if (id == null || id.isEmpty()) return null;
        return resolve(cutScenes, id, "cutScene");
    }

    private List<Chapter> readChapters(
            Element root,
            Map<String, Location> locations,
            Map<String, Item> items,
            Map<String, Permission> permissions,
            Map<String, CutScene> cutScenes
    ) {
        List<Chapter> result = new ArrayList<>();
        Element container = childElement(root, "chapters");
        if (container == null) return result;
        for (Element e : children(container, "chapter")) {
            result.add(readChapter(e, locations, items, permissions, cutScenes));
        }
        return result;
    }

    private Chapter readChapter(
            Element chapterEl,
            Map<String, Location> locations,
            Map<String, Item> items,
            Map<String, Permission> permissions,
            Map<String, CutScene> cutScenes
    ) {
        String id = chapterEl.getAttribute("id");
        int position = Integer.parseInt(chapterEl.getAttribute("position"));

        Element startRef = childElement(chapterEl, "startLocationRef");
        Location startLocation = resolve(locations, startRef.getAttribute("ref"), "location");

        return new Chapter(
                id,
                position,
                textOf(chapterEl, "name"),
                textOf(chapterEl, "description"),
                textOf(chapterEl, "aiHints"),
                startLocation,
                textOf(chapterEl, "startTime"),
                readRefs(chapterEl, "locations", "locationRef", locations, "location"),
                readRefs(chapterEl, "items", "itemRef", items, "item"),
                readTasks(chapterEl, permissions, cutScenes),
                readOptionalCutSceneRef(chapterEl, "startCutSceneRef", cutScenes),
                readOptionalCutSceneRef(chapterEl, "endCutSceneRef", cutScenes)
        );
    }

    private List<Task> readTasks(
            Element chapterEl,
            Map<String, Permission> permissions,
            Map<String, CutScene> cutScenes
    ) {
        List<Task> result = new ArrayList<>();
        Element container = childElement(chapterEl, "tasks");
        if (container == null) return result;
        for (Element taskEl : children(container, "task")) {
            result.add(new Task(
                    taskEl.getAttribute("id"),
                    textOf(taskEl, "name"),
                    textOf(taskEl, "description"),
                    textOf(taskEl, "purpose"),
                    Boolean.parseBoolean(textOf(taskEl, "required")),
                    readRefs(taskEl, "requiredPermissions", "permissionRef", permissions, "permission"),
                    readRefs(taskEl, "grantedPermissions", "permissionRef", permissions, "permission"),
                    readOptionalCutSceneRef(taskEl, "startCutSceneRef", cutScenes),
                    readOptionalCutSceneRef(taskEl, "endCutSceneRef", cutScenes)
            ));
        }
        return result;
    }

    private <T> List<T> readRefs(
            Element parent,
            String containerName,
            String refName,
            Map<String, T> entityById,
            String entityKind
    ) {
        List<T> result = new ArrayList<>();
        Element container = childElement(parent, containerName);
        if (container == null) return result;
        for (Element refEl : children(container, refName)) {
            result.add(resolve(entityById, refEl.getAttribute("ref"), entityKind));
        }
        return result;
    }

    private <T> T resolve(Map<String, T> map, String id, String entityKind) {
        T value = map.get(id);
        if (value == null) {
            throw new IllegalStateException("Reference to unknown " + entityKind + ": '" + id + "'");
        }
        return value;
    }

    private Element childElement(Element parent, String localName) {
        NodeList nodes = parent.getElementsByTagNameNS(NS, localName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getParentNode() == parent) {
                return (Element) n;
            }
        }
        return null;
    }

    private List<Element> children(Element parent, String localName) {
        List<Element> result = new ArrayList<>();
        NodeList nodes = parent.getElementsByTagNameNS(NS, localName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getParentNode() == parent) {
                result.add((Element) n);
            }
        }
        return result;
    }

    private String textOf(Element parent, String localName) {
        Element e = childElement(parent, localName);
        return e == null ? "" : e.getTextContent();
    }

    private static class ThrowingErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException e) throws SAXException { throw e; }
        @Override
        public void error(SAXParseException e) throws SAXException { throw e; }
        @Override
        public void fatalError(SAXParseException e) throws SAXException { throw e; }
    }
}
