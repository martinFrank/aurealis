import type { Adventure, Chapter, LocalizedPerson, Location, Task } from './types';

export function sanitizeAdventure(adventure: Adventure): Adventure {
  const permIds = new Set(adventure.permissions.map((p) => p.id));
  const personIds = new Set(adventure.persons.map((p) => p.id));
  const itemIds = new Set(adventure.items.map((i) => i.id));
  const locationIds = new Set(adventure.locations.map((l) => l.id));
  const cutSceneIds = new Set(adventure.cutScenes.map((c) => c.id));

  const keepIds = (ids: string[], known: Set<string>) =>
    ids.filter((id) => known.has(id));

  const keepOrEmpty = (id: string, known: Set<string>) =>
    id && known.has(id) ? id : '';

  const locations: Location[] = adventure.locations.map((l) => ({
    ...l,
    requiredPermissionIds: keepIds(l.requiredPermissionIds, permIds),
    persons: l.persons
      .filter((lp) => personIds.has(lp.personId))
      .map(
        (lp): LocalizedPerson => ({
          ...lp,
          requiredPermissionId: keepOrEmpty(lp.requiredPermissionId, permIds),
        }),
      ),
  }));

  const chapters: Chapter[] = adventure.chapters.map((c) => ({
    ...c,
    startLocationId: keepOrEmpty(c.startLocationId, locationIds),
    locationIds: keepIds(c.locationIds, locationIds),
    itemIds: keepIds(c.itemIds, itemIds),
    requiredPermissionIds: keepIds(c.requiredPermissionIds, permIds),
    startCutSceneId: keepOrEmpty(c.startCutSceneId, cutSceneIds),
    endCutSceneId: keepOrEmpty(c.endCutSceneId, cutSceneIds),
    tasks: c.tasks.map(
      (t): Task => ({
        ...t,
        requiredPermissionIds: keepIds(t.requiredPermissionIds, permIds),
        grantedPermissionIds: keepIds(t.grantedPermissionIds, permIds),
        startCutSceneId: keepOrEmpty(t.startCutSceneId, cutSceneIds),
        endCutSceneId: keepOrEmpty(t.endCutSceneId, cutSceneIds),
      }),
    ),
  }));

  return { ...adventure, locations, chapters };
}
