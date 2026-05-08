import type { CutScene, Item, Permission, Person } from '../types';
import { EntityCard } from './EntityCard';

export function PersonCard({
  value,
  onChange,
  onDelete,
}: {
  value: Person;
  onChange: (v: Person) => void;
  onDelete: () => void;
}) {
  return (
    <EntityCard
      id={value.id}
      title={value.name}
      subtitle={value.role}
      onDelete={onDelete}
    >
      <div className="row">
        <div>
          <label>Name</label>
          <input
            value={value.name}
            onChange={(e) => onChange({ ...value, name: e.target.value })}
          />
        </div>
        <div>
          <label>Role</label>
          <input
            value={value.role}
            onChange={(e) => onChange({ ...value, role: e.target.value })}
          />
        </div>
      </div>
      <div className="row">
        <div>
          <label>Appearance</label>
          <textarea
            value={value.appearance}
            onChange={(e) => onChange({ ...value, appearance: e.target.value })}
          />
        </div>
        <div>
          <label>Personality</label>
          <textarea
            value={value.personality}
            onChange={(e) => onChange({ ...value, personality: e.target.value })}
          />
        </div>
      </div>
      <div className="row full">
        <div>
          <label>AI Hints</label>
          <textarea
            value={value.aiHints}
            onChange={(e) => onChange({ ...value, aiHints: e.target.value })}
          />
        </div>
      </div>
    </EntityCard>
  );
}

export function ItemCard({
  value,
  onChange,
  onDelete,
}: {
  value: Item;
  onChange: (v: Item) => void;
  onDelete: () => void;
}) {
  return (
    <EntityCard id={value.id} title={value.name} onDelete={onDelete}>
      <div className="row">
        <div>
          <label>Name</label>
          <input
            value={value.name}
            onChange={(e) => onChange({ ...value, name: e.target.value })}
          />
        </div>
        <div>
          <label>AI Hints</label>
          <input
            value={value.aiHints}
            onChange={(e) => onChange({ ...value, aiHints: e.target.value })}
          />
        </div>
      </div>
      <div className="row">
        <div>
          <label>Appearance</label>
          <textarea
            value={value.appearance}
            onChange={(e) => onChange({ ...value, appearance: e.target.value })}
          />
        </div>
        <div>
          <label>Purpose</label>
          <textarea
            value={value.purpose}
            onChange={(e) => onChange({ ...value, purpose: e.target.value })}
          />
        </div>
      </div>
    </EntityCard>
  );
}

export function CutSceneCard({
  value,
  onChange,
  onDelete,
}: {
  value: CutScene;
  onChange: (v: CutScene) => void;
  onDelete: () => void;
}) {
  return (
    <EntityCard id={value.id} title={value.name} onDelete={onDelete}>
      <div className="row full">
        <div>
          <label>Name</label>
          <input
            value={value.name}
            onChange={(e) => onChange({ ...value, name: e.target.value })}
          />
        </div>
      </div>
      <div className="row full">
        <div>
          <label>Text</label>
          <textarea
            value={value.text}
            onChange={(e) => onChange({ ...value, text: e.target.value })}
          />
        </div>
      </div>
    </EntityCard>
  );
}

export function PermissionCard({
  value,
  onChange,
  onDelete,
}: {
  value: Permission;
  onChange: (v: Permission) => void;
  onDelete: () => void;
}) {
  return (
    <EntityCard id={value.id} title={value.name} onDelete={onDelete}>
      <div className="row full">
        <div>
          <label>Name</label>
          <input
            value={value.name}
            onChange={(e) => onChange({ ...value, name: e.target.value })}
          />
        </div>
      </div>
      <div className="row full">
        <div>
          <label>Description</label>
          <textarea
            value={value.description}
            onChange={(e) => onChange({ ...value, description: e.target.value })}
          />
        </div>
      </div>
    </EntityCard>
  );
}
