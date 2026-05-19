import type { CutScene, Task, TaskPredicate } from '../types';
import { EntityCard } from './EntityCard';
import { RefMultiSelect, RefSelect } from './RefMultiSelect';

type Props = {
  value: Task;
  taskPredicates: TaskPredicate[];
  cutScenes: CutScene[];
  onChange: (v: Task) => void;
  onDelete: () => void;
};

export function TaskCard({ value, taskPredicates, cutScenes, onChange, onDelete }: Props) {
  const options = taskPredicates.map((p) => ({ id: p.id, label: p.name }));
  const csOpts = cutScenes.map((c) => ({ id: c.id, label: c.name }));

  const subtitleParts: string[] = [];
  if (value.required) subtitleParts.push('required');
  if (value.requiredTaskPredicateIds.length)
    subtitleParts.push(`req ${value.requiredTaskPredicateIds.length}`);
  if (value.grantedTaskPredicateIds.length)
    subtitleParts.push(`grants ${value.grantedTaskPredicateIds.length}`);
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
          label="Required Task Predicates"
          options={options}
          selectedIds={value.requiredTaskPredicateIds}
          onChange={(ids) => onChange({ ...value, requiredTaskPredicateIds: ids })}
        />
        <RefMultiSelect
          label="Granted Task Predicates"
          options={options}
          selectedIds={value.grantedTaskPredicateIds}
          onChange={(ids) => onChange({ ...value, grantedTaskPredicateIds: ids })}
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
