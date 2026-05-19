import type {
  Adventure,
  Chapter,
  CutScene,
  Item,
  LocalizedPerson,
  Location,
  Person,
  Task,
  TaskPredicate,
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
  taskPredicates: [],
  cutScenes: [],
  chapters: [],
});

export const newLocation = (): Location => ({
  id: `loc.${shortId()}`,
  name: '',
  description: '',
  aiHints: '',
  requiredTaskPredicateIds: [],
  persons: [],
});

export const newLocalizedPerson = (): LocalizedPerson => ({
  personId: '',
  requiredTaskPredicateId: '',
});

export const newPerson = (): Person => ({
  id: `per.${shortId()}`,
  name: '',
  appearance: '',
  personality: '',
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

export const newTaskPredicate = (): TaskPredicate => ({
  id: `prm.${shortId()}`,
  name: '',
  description: '',
});

export const newCutScene = (): CutScene => ({
  id: `cs.${shortId()}`,
  name: '',
  text: '',
});

export const newTask = (): Task => ({
  id: `tsk.${shortId()}`,
  name: '',
  description: '',
  purpose: '',
  required: false,
  requiredTaskPredicateIds: [],
  grantedTaskPredicateIds: [],
  startCutSceneId: '',
  endCutSceneId: '',
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
  itemIds: [],
  tasks: [],
  requiredTaskPredicateIds: [],
  startCutSceneId: '',
  endCutSceneId: '',
});
