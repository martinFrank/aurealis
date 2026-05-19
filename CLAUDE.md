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
