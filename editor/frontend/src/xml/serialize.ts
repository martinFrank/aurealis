import type { Adventure, Chapter, Task } from '../types';

const NS = 'https://github.com/martinfrank/elitegames/aurealis/adventure';

export function serializeAdventureXml(adventure: Adventure): string {
  const doc = document.implementation.createDocument(NS, 'adventure', null);
  const root = doc.documentElement;

  appendText(doc, root, 'title', adventure.title);
  appendText(doc, root, 'description', adventure.description);
  appendText(doc, root, 'author', adventure.author);

  const locations = appendChild(doc, root, 'locations');
  for (const l of adventure.locations) {
    const el = appendChild(doc, locations, 'location');
    el.setAttribute('id', l.id);
    appendText(doc, el, 'name', l.name);
    appendText(doc, el, 'description', l.description);
    appendText(doc, el, 'aiHints', l.aiHints);
    appendRefs(doc, el, 'requiredPermissions', 'permissionRef', l.requiredPermissionIds);

    const personsContainer = appendChild(doc, el, 'persons');
    for (const lp of l.persons) {
      const lpEl = appendChild(doc, personsContainer, 'localizedPerson');
      const pRef = appendChild(doc, lpEl, 'personRef');
      pRef.setAttribute('ref', lp.personId);
      const permRef = appendChild(doc, lpEl, 'permissionRef');
      permRef.setAttribute('ref', lp.requiredPermissionId);
    }
  }

  const persons = appendChild(doc, root, 'persons');
  for (const p of adventure.persons) {
    const el = appendChild(doc, persons, 'person');
    el.setAttribute('id', p.id);
    appendText(doc, el, 'name', p.name);
    appendText(doc, el, 'appearance', p.appearance);
    appendText(doc, el, 'personality', p.personality);
    appendText(doc, el, 'role', p.role);
    appendText(doc, el, 'aiHints', p.aiHints);
  }

  const items = appendChild(doc, root, 'items');
  for (const it of adventure.items) {
    const el = appendChild(doc, items, 'item');
    el.setAttribute('id', it.id);
    appendText(doc, el, 'name', it.name);
    appendText(doc, el, 'appearance', it.appearance);
    appendText(doc, el, 'purpose', it.purpose);
    appendText(doc, el, 'aiHints', it.aiHints);
  }

  const permissions = appendChild(doc, root, 'permissions');
  for (const p of adventure.permissions) {
    const el = appendChild(doc, permissions, 'permission');
    el.setAttribute('id', p.id);
    appendText(doc, el, 'name', p.name);
    appendText(doc, el, 'description', p.description);
    appendText(doc, el, 'grantCondition', p.grantCondition);
    appendText(doc, el, 'state', p.state);
  }

  const chapters = appendChild(doc, root, 'chapters');
  for (const c of adventure.chapters) {
    appendChapter(doc, chapters, c);
  }

  return prettyPrint(doc);
}

function appendChapter(doc: XMLDocument, parent: Element, c: Chapter): void {
  const el = appendChild(doc, parent, 'chapter');
  el.setAttribute('id', c.id);
  el.setAttribute('position', String(c.position));
  appendText(doc, el, 'name', c.name);
  appendText(doc, el, 'description', c.description);
  appendText(doc, el, 'aiHints', c.aiHints);
  appendText(doc, el, 'startTime', c.startTime);

  const startRef = appendChild(doc, el, 'startLocationRef');
  startRef.setAttribute('ref', c.startLocationId);

  appendRefs(doc, el, 'locations', 'locationRef', c.locationIds);
  appendRefs(doc, el, 'items', 'itemRef', c.itemIds);

  const tasks = appendChild(doc, el, 'tasks');
  for (const t of c.tasks) {
    appendTask(doc, tasks, t);
  }
}

function appendTask(doc: XMLDocument, parent: Element, t: Task): void {
  const el = appendChild(doc, parent, 'task');
  el.setAttribute('id', t.id);
  appendText(doc, el, 'description', t.description);
  appendText(doc, el, 'purpose', t.purpose);
  appendText(doc, el, 'required', String(t.required));
  appendRefs(doc, el, 'requiredPermissions', 'permissionRef', t.requiredPermissionIds);
  appendRefs(doc, el, 'grantedPermissions', 'permissionRef', t.grantedPermissionIds);
}

function appendRefs(
  doc: XMLDocument,
  parent: Element,
  containerName: string,
  refName: string,
  ids: string[],
): void {
  const container = appendChild(doc, parent, containerName);
  for (const id of ids) {
    const refEl = appendChild(doc, container, refName);
    refEl.setAttribute('ref', id);
  }
}

function appendChild(doc: XMLDocument, parent: Element, name: string): Element {
  const el = doc.createElementNS(NS, name);
  parent.appendChild(el);
  return el;
}

function appendText(doc: XMLDocument, parent: Element, name: string, value: string): void {
  const el = appendChild(doc, parent, name);
  el.textContent = value;
}

function prettyPrint(doc: XMLDocument): string {
  const raw = new XMLSerializer().serializeToString(doc);
  return '<?xml version="1.0" encoding="UTF-8"?>\n' + indent(raw);
}

function indent(xml: string): string {
  const tokens = xml.replace(/>\s*</g, '><').replace(/></g, '>\n<').split('\n');
  let depth = 0;
  return tokens
    .map((line) => {
      const isClose = /^<\/[^>]+>/.test(line);
      const isSelfClose = /\/>$/.test(line) || /<\?xml/.test(line);
      const isOpen = /^<[^!?/][^>]*[^/]>$/.test(line);
      if (isClose) depth = Math.max(0, depth - 1);
      const result = '  '.repeat(depth) + line;
      if (isOpen && !isSelfClose) depth += 1;
      return result;
    })
    .join('\n');
}
