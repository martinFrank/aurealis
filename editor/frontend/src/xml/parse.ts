import type {
  Adventure,
  Chapter,
  CutScene,
  Item,
  LocalizedPerson,
  Location,
  Permission,
  PermissionState,
  Person,
  Task,
} from '../types';
import { sanitizeAdventure } from '../sanitize';

const NS = 'https://github.com/martinfrank/elitegames/aurealis/adventure';

const isElement = (n: Node): n is Element => n.nodeType === 1;

const child = (el: Element, name: string): Element | null => {
  for (const n of Array.from(el.childNodes)) {
    if (isElement(n) && n.localName === name && n.namespaceURI === NS) return n;
  }
  return null;
};

const childrenOf = (el: Element, name: string): Element[] =>
  Array.from(el.childNodes)
    .filter(isElement)
    .filter((e) => e.localName === name && e.namespaceURI === NS);

const text = (el: Element, name: string): string => child(el, name)?.textContent ?? '';

const refs = (el: Element, container: string, refName: string): string[] => {
  const c = child(el, container);
  if (!c) return [];
  return childrenOf(c, refName).map((r) => r.getAttribute('ref') ?? '');
};

export function parseAdventureXml(xml: string): Adventure {
  const doc = new DOMParser().parseFromString(xml, 'application/xml');
  const error = doc.querySelector('parsererror');
  if (error) throw new Error(`Invalid XML: ${error.textContent ?? 'parse error'}`);

  const root = doc.documentElement;
  if (root.namespaceURI !== NS || root.localName !== 'adventure') {
    throw new Error(`Not an adventure document (root: ${root.localName})`);
  }

  const locations: Location[] =
    childrenOf(child(root, 'locations') ?? root, 'location').map((e) => ({
      id: e.getAttribute('id') ?? '',
      name: text(e, 'name'),
      description: text(e, 'description'),
      aiHints: text(e, 'aiHints'),
      requiredPermissionIds: refs(e, 'requiredPermissions', 'permissionRef'),
      persons: parseLocalizedPersons(e),
    }));

  const persons: Person[] =
    childrenOf(child(root, 'persons') ?? root, 'person').map((e) => ({
      id: e.getAttribute('id') ?? '',
      name: text(e, 'name'),
      appearance: text(e, 'appearance'),
      personality: text(e, 'personality'),
      role: text(e, 'role'),
      aiHints: text(e, 'aiHints'),
    }));

  const items: Item[] =
    childrenOf(child(root, 'items') ?? root, 'item').map((e) => ({
      id: e.getAttribute('id') ?? '',
      name: text(e, 'name'),
      appearance: text(e, 'appearance'),
      purpose: text(e, 'purpose'),
      aiHints: text(e, 'aiHints'),
    }));

  const permissions: Permission[] =
    childrenOf(child(root, 'permissions') ?? root, 'permission').map((e) => ({
      id: e.getAttribute('id') ?? '',
      name: text(e, 'name'),
      description: text(e, 'description'),
      state: (text(e, 'state') as PermissionState) || 'DENIED',
    }));

  const cutScenes: CutScene[] =
    childrenOf(child(root, 'cutScenes') ?? root, 'cutScene').map((e) => ({
      id: e.getAttribute('id') ?? '',
      name: text(e, 'name'),
      text: text(e, 'text'),
    }));

  const chapters: Chapter[] =
    childrenOf(child(root, 'chapters') ?? root, 'chapter').map((e) => ({
      id: e.getAttribute('id') ?? '',
      position: Number.parseInt(e.getAttribute('position') ?? '0', 10),
      name: text(e, 'name'),
      description: text(e, 'description'),
      aiHints: text(e, 'aiHints'),
      startLocationId: child(e, 'startLocationRef')?.getAttribute('ref') ?? '',
      startTime: text(e, 'startTime'),
      locationIds: refs(e, 'locations', 'locationRef'),
      itemIds: refs(e, 'items', 'itemRef'),
      tasks: parseTasks(e),
      requiredPermissionIds: refs(e, 'requiredPermissions', 'permissionRef'),
      startCutSceneId: child(e, 'startCutSceneRef')?.getAttribute('ref') ?? '',
      endCutSceneId: child(e, 'endCutSceneRef')?.getAttribute('ref') ?? '',
    }));

  return sanitizeAdventure({
    title: text(root, 'title'),
    description: text(root, 'description'),
    author: text(root, 'author'),
    locations,
    persons,
    items,
    permissions,
    cutScenes,
    chapters,
  });
}

function parseLocalizedPersons(locationEl: Element): LocalizedPerson[] {
  const container = child(locationEl, 'persons');
  if (!container) return [];
  return childrenOf(container, 'localizedPerson').map((e) => ({
    personId: child(e, 'personRef')?.getAttribute('ref') ?? '',
    requiredPermissionId: child(e, 'permissionRef')?.getAttribute('ref') ?? '',
  }));
}

function parseTasks(chapterEl: Element): Task[] {
  const container = child(chapterEl, 'tasks');
  if (!container) return [];
  return childrenOf(container, 'task').map((e) => ({
    id: e.getAttribute('id') ?? '',
    name: text(e, 'name'),
    description: text(e, 'description'),
    purpose: text(e, 'purpose'),
    required: text(e, 'required') === 'true',
    requiredPermissionIds: refs(e, 'requiredPermissions', 'permissionRef'),
    grantedPermissionIds: refs(e, 'grantedPermissions', 'permissionRef'),
    startCutSceneId: child(e, 'startCutSceneRef')?.getAttribute('ref') ?? '',
    endCutSceneId: child(e, 'endCutSceneRef')?.getAttribute('ref') ?? '',
  }));
}
