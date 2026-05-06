import type { CutScene, Permission, Task } from '../types';
import { EntityCard } from './EntityCard';
import { RefMultiSelect, RefSelect } from './RefMultiSelect';

type Props = {
  value: Task;
  permissions: Permission[];
  cutScenes: CutScene[];
  onChange: (v: Task) => void;
  onDelete: () => void;
};

export function TaskCard({ value, permissions, cutScenes, onChange, onDelete }: Props) {
  const options = permissions.map((p) => ({ id: p.id, label: p.name }));
  const csOpts = cutScenes.map((c) => ({ id: c.id, label: c.name }));

  const subtitleParts: string[] = [];
  if (value.required) subtitleParts.push('required');
  if (value.requiredPermissionIds.length)
    subtitleParts.push(`req ${value.requiredPermissionIds.length}`);
  if (value.grantedPermissionIds.length)
    subtitleParts.push(`grants ${value.grantedPermissionIds.length}`);
  const subtitle = subtitleParts.join(' · ');

  const requiredToggle = (
    <label className="inline-checkbox">
      <input
        type="checkbox"
        checked={value.required}
        onChange={(e) => onChange({ ...value, required: e.target.checked })}
      />
      required
    </label>
  );

  return (
    <EntityCard
      id={value.id}
      title={value.name}
      subtitle={subtitle || undefined}
      extras={requiredToggle}
      onDelete={onDelete}
      className="task"
    >
      <div className="row full">
        <div>
          <label>Name</label>
          <input
            value={value.name}
            onChange={(e) => onChange({ ...value, name: e.target.value })}
          />
        </div>
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
      <div className="row">
        <RefSelect
          label="Start CutScene"
          options={csOpts}
          selectedId={value.startCutSceneId}
          onChange={(id) => onChange({ ...value, startCutSceneId: id })}
        />
        <RefSelect
          label="End CutScene"
          options={csOpts}
          selectedId={value.endCutSceneId}
          onChange={(id) => onChange({ ...value, endCutSceneId: id })}
        />
      </div>
    </EntityCard>
  );
}
