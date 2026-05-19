export type TaskPredicate = {
  id: string;
  name: string;
  description: string;
};

export type LocalizedPerson = {
  personId: string;
  requiredTaskPredicateId: string;
};

export type Location = {
  id: string;
  name: string;
  description: string;
  aiHints: string;
  requiredTaskPredicateIds: string[];
  persons: LocalizedPerson[];
};

export type Person = {
  id: string;
  name: string;
  appearance: string;
  personality: string;
  role: string;
  aiHints: string;
};

export type Item = {
  id: string;
  name: string;
  appearance: string;
  purpose: string;
  aiHints: string;
};

export type Task = {
  id: string;
  name: string;
  description: string;
  purpose: string;
  required: boolean;
  requiredTaskPredicateIds: string[];
  grantedTaskPredicateIds: string[];
  startCutSceneId: string;
  endCutSceneId: string;
};

export type CutScene = {
  id: string;
  name: string;
  text: string;
};

export type Chapter = {
  id: string;
  position: number;
  name: string;
  description: string;
  aiHints: string;
  startLocationId: string;
  startTime: string;
  locationIds: string[];
  itemIds: string[];
  tasks: Task[];
  requiredTaskPredicateIds: string[];
  startCutSceneId: string;
  endCutSceneId: string;
};

export type Adventure = {
  title: string;
  description: string;
  author: string;
  locations: Location[];
  persons: Person[];
  items: Item[];
  taskPredicates: TaskPredicate[];
  cutScenes: CutScene[];
  chapters: Chapter[];
};
