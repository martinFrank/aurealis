# Aurealis

## Tests

Jede nicht-triviale Logik muss durch Tests abgedeckt sein. Insbesondere:
- Das Engine-Modul (`engine/`) verwendet JUnit 5 (Jupiter); Tests liegen unter `engine/src/test/java/...`, Test-Ressourcen unter `engine/src/test/resources/...`.
- Reader/Writer für XML-Adventures werden mit Beispiel-XML-Dateien als Round-Trip / Resolver-Tests geprüft (Referenzen müssen identitätsgleich auf Adventure-Objekte zeigen, nicht nur strukturgleich).
- Validierungsfehler (Schema-Verstöße, dangling Refs) müssen als negative Tests abgedeckt sein.

## Workflow

Abenteuer werden im **Editor** (`editor/frontend`, React + TypeScript) erfasst oder aus bestehendem XML importiert und als XML exportiert. Die **Engine** (`engine/`, Java) importiert diese XML-Dateien, um die Abenteuer zu spielen. Das XSD-Schema (`engine/src/main/resources/schema/adventure.xsd`) ist die gemeinsame Vertragsdefinition zwischen beiden Seiten.

## Architektur

### Adventure-Modell: zentrale Definition, Referenzen pro Chapter

`Locations`, `Persons`, `Items` und `TaskPredicates` werden **einmalig auf Adventure-Ebene** definiert. Chapter referenzieren diese Entitäten nur per ID — sie definieren sie nicht selbst neu.

- Adventure (`engine/.../adventure/Adventure.java`) hält die kanonischen Listen: `locations`, `persons`, `items`, `taskPredicates`.
- Chapter (`engine/.../adventure/Chapter.java`) hält Referenzen auf eine Teilmenge dieser Entitäten plus eigene `tasks` (Tasks sind chapter-lokal).
- Im XSD-Schema (`engine/src/main/resources/schema/adventure.xsd`) wird das über `xs:key` (auf Adventure-Ebene) und `xs:keyref` (in den Chapter-Listen `locationRef`, `personRef`, `itemRef`) abgebildet. TaskPredicate-Refs in Tasks zeigen ebenfalls auf die Adventure-weite TaskPredicate-Liste.

**Warum:** Die gleiche Person/Location/Item kann in mehreren Chaptern auftauchen — eine zentrale Definition vermeidet Duplikate und macht Konsistenzänderungen trivial. Tasks bleiben chapter-lokal, weil sie zum Handlungsbogen eines Chapters gehören und nicht wiederverwendet werden.

**Konsequenz für den Editor (`editor/frontend`):** Der Editor pflegt die Adventure-weiten Listen als oberste Ebene; Chapter-Formulare bieten Auswahl-Dropdowns/Multiselects, deren Optionen aus diesen Listen gespeist werden.

## AI-Spielleitung

Das Abenteuer wird nicht über ein festes Befehlssystem gespielt, sondern über einen **LLM-Agenten als Spielleiter**. Der Spieler kommuniziert in natürlicher Sprache; der Agent narriert, fragt nach, beschreibt Szenen und reagiert auf Aktionen.

### Pipeline

Jede Spielereingabe durchläuft fünf Stufen:

1. **Input Interpreter** — wandelt freien Spielertext in eine strukturierte Intent um (z. B. `MoveTo(locationId)`, `TalkTo(personId)`, `Inspect(itemId)`, `UseItem(itemId, targetId)`, `FreeForm(text)`). Nutzt `langchain4j` Structured Output / Tool-Calling gegen das aktuell sichtbare Aktions­vokabular (erreichbare Locations, anwesende Personen, verfügbare Items).
2. **Context Resolver / Retriever** — sammelt den für die Eingabe relevanten Spielzustand: aktuelle Location, anwesende Personen/Items, aktive Tasks, erfüllte/offene `TaskPredicates`, relevanten Chat-Verlauf und die `aiHints` aller beteiligten Entitäten.
3. **Action Stage** — die **Engine** (`engine/.../game/`) validiert die Intent deterministisch gegen den Spielzustand: Ist die Ziel-Location erreichbar? Erfüllt die Party die nötigen `TaskPredicates`? Existiert die Person an dieser Location? Ergebnis ist ein strukturiertes `ActionResult` (erlaubt/abgelehnt + State-Delta: neue Predicates, Location-Wechsel, etc.) — **keine LLM-Beteiligung**.
4. **Response Generator** — bekommt Context + `ActionResult` und erzeugt die narrative Antwort. Auf abgelehnte Aktionen reagiert er in-character ("die Tür ist verschlossen"), auf erlaubte beschreibt er das Ergebnis und den neuen Spielzustand.
5. **Response** — wird an den Spieler geliefert und in `Chat` persistiert; State-Deltas werden in `Session` angewendet.

### Verantwortungstrennung: Engine entscheidet, LLM narriert

**Was ist möglich → entscheidet die Engine (deterministisch).** Was passiert erzählerisch → narriert das LLM (kreativ). Diese Trennung ist nicht verhandelbar:

- Die Engine ist autoritativ für alle Spielmechanik-Entscheidungen: `TaskPredicate`-Wechsel, Chapter-Übergänge, Location-Wechsel, Item-Erwerb/-Verlust.
- Das LLM darf **keine** State-Mutationen direkt vornehmen — es schlägt höchstens via Intent vor, die Engine validiert und entscheidet.
- Folge: Spielmechanik ist testbar (siehe `SessionTest`, `ChapterTracker`-Tests), und das LLM kann nicht halluzinieren, dass eine verschlossene Tür plötzlich offen ist.

### Kreativer Spielraum via `aiHints`

Jede `Location`, `Person`, `Item` und ggf. `Task` trägt ein `aiHints`-Feld (Freitext). Dieses Feld beschreibt explizit, **wo das LLM erfinden darf** — z. B. "auf dem Marktplatz darfst du temporär Händler, Bettler, Spielleute erfinden" oder "in dieser Taverne sind die NPCs bis auf den Wirt austauschbar".

- `aiHints` werden vom Context Resolver an die Response-Generator-Stufe weitergereicht und definieren den narrativen Freiraum.
- Vom Adventure-Autor nicht erlaubte Erfindungen (z. B. neue Locations, plot-relevante Personen) bleiben für das LLM tabu — das wird per System-Prompt durchgesetzt.
- Temporär erfundene Entitäten (z. B. ein zufälliger Marktstand-Händler) leben nur im Chat-Kontext, nicht im persistenten `Adventure`-Modell.

### LLM-Anbindung

- **Provider:** Ollama (lokal), bereits via `langchain4j-ollama` in `engine/pom.xml` eingebunden.
- **Modellwahl pro Stage darf unterschiedlich sein:** der Input Interpreter braucht ein Modell mit verlässlichem Structured Output (kleineres/schnelleres Modell ok), der Response Generator profitiert von einem größeren Modell mit besserem Sprachgefühl.
- **Kein Cloud-Modell** geplant — alles lokal.
