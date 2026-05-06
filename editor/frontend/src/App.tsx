import { useRef, useState } from 'react';
import type { Adventure, Chapter, Item, Location, Permission, Person } from './types';
import {
  newAdventure,
  newChapter,
  newItem,
  newLocation,
  newPermission,
  newPerson,
} from './factories';
import { parseAdventureXml } from './xml/parse';
import { serializeAdventureXml } from './xml/serialize';
import { Section } from './components/Section';
import {
  ItemCard,
  PermissionCard,
  PersonCard,
} from './components/EntityEditors';
import { LocationCard } from './components/LocationCard';
import { ChapterCard } from './components/ChapterCard';

export default function App() {
  const [adventure, setAdventure] = useState<Adventure>(newAdventure);
  const [error, setError] = useState<string | null>(null);
  const fileInput = useRef<HTMLInputElement>(null);

  const update = (patch: Partial<Adventure>) => setAdventure({ ...adventure, ...patch });

  const updateAt = <K extends keyof Adventure>(
    key: K,
    idx: number,
    value: Adventure[K] extends (infer T)[] ? T : never,
  ) => {
    const list = (adventure[key] as unknown[]).slice();
    list[idx] = value;
    setAdventure({ ...adventure, [key]: list } as Adventure);
  };

  const removeAt = (key: keyof Adventure, idx: number) => {
    const list = (adventure[key] as unknown[]).slice();
    list.splice(idx, 1);
    setAdventure({ ...adventure, [key]: list } as Adventure);
  };

  const onImport = async (file: File) => {
    setError(null);
    try {
      const text = await file.text();
      setAdventure(parseAdventureXml(text));
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    }
  };

  const onExport = () => {
    const xml = serializeAdventureXml(adventure);
    const blob = new Blob([xml], { type: 'application/xml' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = (adventure.title || 'adventure').replace(/\s+/g, '_') + '.xml';
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="app">
      <div className="toolbar">
        <h1>Aurealis Adventure Editor</h1>
        <button onClick={() => setAdventure(newAdventure())}>Neu</button>
        <button onClick={() => fileInput.current?.click()}>XML importieren…</button>
        <input
          ref={fileInput}
          type="file"
          accept=".xml,application/xml,text/xml"
          style={{ display: 'none' }}
          onChange={(e) => {
            const f = e.target.files?.[0];
            if (f) onImport(f);
            e.target.value = '';
          }}
        />
        <button className="primary" onClick={onExport}>
          XML exportieren
        </button>
      </div>

      {error && <div className="error">{error}</div>}

      <Section title="Adventure">
        <div className="row">
          <div>
            <label>Titel</label>
            <input
              value={adventure.title}
              onChange={(e) => update({ title: e.target.value })}
            />
          </div>
          <div>
            <label>Autor</label>
            <input
              value={adventure.author}
              onChange={(e) => update({ author: e.target.value })}
            />
          </div>
        </div>
        <div className="row full">
          <div>
            <label>Beschreibung</label>
            <textarea
              value={adventure.description}
              onChange={(e) => update({ description: e.target.value })}
            />
          </div>
        </div>
      </Section>

      <Section title="Locations" count={adventure.locations.length}>
        {adventure.locations.map((l, idx) => (
          <LocationCard
            key={l.id}
            value={l}
            adventure={adventure}
            onChange={(v: Location) => updateAt('locations', idx, v)}
            onDelete={() => removeAt('locations', idx)}
          />
        ))}
        <button
          onClick={() =>
            update({ locations: [...adventure.locations, newLocation()] })
          }
        >
          + Location
        </button>
      </Section>

      <Section title="Persons" count={adventure.persons.length}>
        {adventure.persons.map((p, idx) => (
          <PersonCard
            key={p.id}
            value={p}
            onChange={(v: Person) => updateAt('persons', idx, v)}
            onDelete={() => removeAt('persons', idx)}
          />
        ))}
        <button
          onClick={() => update({ persons: [...adventure.persons, newPerson()] })}
        >
          + Person
        </button>
      </Section>

      <Section title="Items" count={adventure.items.length}>
        {adventure.items.map((it, idx) => (
          <ItemCard
            key={it.id}
            value={it}
            onChange={(v: Item) => updateAt('items', idx, v)}
            onDelete={() => removeAt('items', idx)}
          />
        ))}
        <button onClick={() => update({ items: [...adventure.items, newItem()] })}>
          + Item
        </button>
      </Section>

      <Section title="Permissions" count={adventure.permissions.length}>
        {adventure.permissions.map((p, idx) => (
          <PermissionCard
            key={p.id}
            value={p}
            onChange={(v: Permission) => updateAt('permissions', idx, v)}
            onDelete={() => removeAt('permissions', idx)}
          />
        ))}
        <button
          onClick={() =>
            update({ permissions: [...adventure.permissions, newPermission()] })
          }
        >
          + Permission
        </button>
      </Section>

      <Section title="Chapters" count={adventure.chapters.length}>
        {adventure.chapters.map((c, idx) => (
          <ChapterCard
            key={c.id}
            value={c}
            adventure={adventure}
            onChange={(v: Chapter) => updateAt('chapters', idx, v)}
            onDelete={() => removeAt('chapters', idx)}
          />
        ))}
        <button
          onClick={() =>
            update({
              chapters: [
                ...adventure.chapters,
                newChapter(adventure.chapters.length + 1),
              ],
            })
          }
        >
          + Chapter
        </button>
      </Section>
    </div>
  );
}
