import type {
  Adventure,
  Chapter,
  Item,
  Location,
  Permission,
  Person,
  Task,
} from './types';

const shortId = () =>
  (typeof crypto !== 'undefined' && 'randomUUID' in crypto
    ? crypto.randomUUID()
    : Math.random().toString(36)).slice(0, 8);

export const newAdventure = (): Adventure => ({
  title: '',
  description: '',
  author: '',
  locations: [],
  persons: [],
  items: [],
  permissions: [],
  chapters: [],
});

export const newLocation = (): Location => ({
  id: `loc.${shortId()}`,
  name: '',
  description: '',
  aiHints: '',
});

export const newPerson = (): Person => ({
  id: `per.${shortId()}`,
  name: '',
  appearance: '',
  role: '',
  aiHints: '',
});

export const newItem = (): Item => ({
  id: `itm.${shortId()}`,
  name: '',
  appearance: '',
  purpose: '',
  aiHints: '',
});

export const newPermission = (): Permission => ({
  id: `prm.${shortId()}`,
  name: '',
  description: '',
  grantCondition: '',
  state: 'DENIED',
});

export const newTask = (): Task => ({
  id: `tsk.${shortId()}`,
  description: '',
  purpose: '',
  required: false,
  requiredPermissionIds: [],
  grantedPermissionIds: [],
});

export const newChapter = (position: number): Chapter => ({
  id: `ch.${shortId()}`,
  position,
  name: '',
  description: '',
  aiHints: '',
  startLocationId: '',
  startTime: '',
  locationIds: [],
  personIds: [],
  itemIds: [],
  tasks: [],
});
