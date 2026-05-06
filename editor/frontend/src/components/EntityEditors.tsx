import type { Item, Permission, Person } from '../types';

type EntityHeaderProps = {
  id: string;
  onDelete: () => void;
};

function EntityHeader({ id, onDelete }: EntityHeaderProps) {
  return (
    <div className="entity-header">
      <span className="id">{id}</span>
      <button className="danger" onClick={onDelete}>
        löschen
      </button>
    </div>
  );
}

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
    <div className="entity">
      <EntityHeader id={value.id} onDelete={onDelete} />
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
      <div className="row">
        <div>
          <label>AI Hints</label>
          <textarea
            value={value.aiHints}
            onChange={(e) => onChange({ ...value, aiHints: e.target.value })}
          />
        </div>
      </div>
    </div>
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
    <div className="entity">
      <EntityHeader id={value.id} onDelete={onDelete} />
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
    </div>
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
    <div className="entity">
      <EntityHeader id={value.id} onDelete={onDelete} />
      <div className="row">
        <div>
          <label>Name</label>
          <input
            value={value.name}
            onChange={(e) => onChange({ ...value, name: e.target.value })}
          />
        </div>
        <div>
          <label>State</label>
          <select
            value={value.state}
            onChange={(e) =>
              onChange({ ...value, state: e.target.value as Permission['state'] })
            }
          >
            <option value="GRANTED">GRANTED</option>
            <option value="DENIED">DENIED</option>
          </select>
        </div>
      </div>
      <div className="row">
        <div>
          <label>Description</label>
          <textarea
            value={value.description}
            onChange={(e) => onChange({ ...value, description: e.target.value })}
          />
        </div>
        <div>
          <label>Grant Condition</label>
          <textarea
            value={value.grantCondition}
            onChange={(e) => onChange({ ...value, grantCondition: e.target.value })}
          />
        </div>
      </div>
    </div>
  );
}
