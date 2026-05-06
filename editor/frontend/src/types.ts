export type PermissionState = 'GRANTED' | 'DENIED';

export type Permission = {
  id: string;
  name: string;
  description: string;
  grantCondition: string;
  state: PermissionState;
};

export type LocalizedPerson = {
  personId: string;
  requiredPermissionId: string;
};

export type Location = {
  id: string;
  name: string;
  description: string;
  aiHints: string;
  requiredPermissionIds: string[];
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
  description: string;
  purpose: string;
  required: boolean;
  requiredPermissionIds: string[];
  grantedPermissionIds: string[];
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
};

export type Adventure = {
  title: string;
  description: string;
  author: string;
  locations: Location[];
  persons: Person[];
  items: Item[];
  permissions: Permission[];
  chapters: Chapter[];
};
