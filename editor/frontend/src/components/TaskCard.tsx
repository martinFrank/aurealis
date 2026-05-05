import type { Permission, Task } from '../types';
import { RefMultiSelect } from './RefMultiSelect';

type Props = {
  value: Task;
  permissions: Permission[];
  onChange: (v: Task) => void;
  onDelete: () => void;
};

export function TaskCard({ value, permissions, onChange, onDelete }: Props) {
  const options = permissions.map((p) => ({ id: p.id, label: p.name }));

  return (
    <div className="entity task">
      <div className="entity-header">
        <span className="id">{value.id}</span>
        <label style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', margin: 0 }}>
          <input
            type="checkbox"
            checked={value.required}
            onChange={(e) => onChange({ ...value, required: e.target.checked })}
            style={{ width: 'auto' }}
          />
          required
        </label>
        <button className="danger" onClick={onDelete}>
          löschen
        </button>
      </div>
      <div className="row">
        <div>
          <label>Description (what)</label>
          <textarea
            value={value.description}
            onChange={(e) => onChange({ ...value, description: e.target.value })}
          />
        </div>
        <div>
          <label>Purpose (why)</label>
          <textarea
            value={value.purpose}
            onChange={(e) => onChange({ ...value, purpose: e.target.value })}
          />
        </div>
      </div>
      <div className="row">
        <RefMultiSelect
          label="Required Permissions"
          options={options}
          selectedIds={value.requiredPermissionIds}
          onChange={(ids) => onChange({ ...value, requiredPermissionIds: ids })}
        />
        <RefMultiSelect
          label="Granted Permissions"
          options={options}
          selectedIds={value.grantedPermissionIds}
          onChange={(ids) => onChange({ ...value, grantedPermissionIds: ids })}
        />
      </div>
    </div>
  );
}
