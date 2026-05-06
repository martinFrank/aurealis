import type { Adventure, Chapter, Task } from '../types';
import { newTask } from '../factories';
import { EntityCard } from './EntityCard';
import { RefMultiSelect, RefSelect } from './RefMultiSelect';
import { TaskCard } from './TaskCard';

type Props = {
  value: Chapter;
  adventure: Adventure;
  onChange: (v: Chapter) => void;
  onDelete: () => void;
};

export function ChapterCard({ value, adventure, onChange, onDelete }: Props) {
  const locOpts = adventure.locations.map((l) => ({ id: l.id, label: l.name }));
  const itmOpts = adventure.items.map((i) => ({ id: i.id, label: i.name }));
  const csOpts = adventure.cutScenes.map((c) => ({ id: c.id, label: c.name }));

  const updateTask = (idx: number, updated: Task) => {
    const tasks = value.tasks.slice();
    tasks[idx] = updated;
    onChange({ ...value, tasks });
  };

  const removeTask = (idx: number) => {
    const tasks = value.tasks.slice();
    tasks.splice(idx, 1);
    onChange({ ...value, tasks });
  };

  const addTask = () => {
    onChange({ ...value, tasks: [...value.tasks, newTask()] });
  };

  const subtitleParts: string[] = [`Pos ${value.position}`];
  if (value.tasks.length) subtitleParts.push(`${value.tasks.length} Task`);
  const subtitle = subtitleParts.join(' · ');

  return (
    <EntityCard
      id={value.id}
      title={value.name}
      subtitle={subtitle}
      onDelete={onDelete}
      deleteLabel="Chapter löschen"
      className="chapter"
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
          <label>Position</label>
          <input
            type="number"
            value={value.position}
            onChange={(e) =>
              onChange({ ...value, position: Number.parseInt(e.target.value, 10) || 0 })
            }
          />
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
          <label>AI Hints</label>
          <textarea
            value={value.aiHints}
            onChange={(e) => onChange({ ...value, aiHints: e.target.value })}
          />
        </div>
      </div>
      <div className="row">
        <div>
          <label>Start Time</label>
          <input
            value={value.startTime}
            onChange={(e) => onChange({ ...value, startTime: e.target.value })}
          />
        </div>
        <RefSelect
          label="Start Location"
          options={locOpts}
          selectedId={value.startLocationId}
          onChange={(id) => onChange({ ...value, startLocationId: id })}
        />
      </div>

      <div className="row">
        <RefMultiSelect
          label="Locations"
          options={locOpts}
          selectedIds={value.locationIds}
          onChange={(ids) => onChange({ ...value, locationIds: ids })}
        />
        <RefMultiSelect
          label="Items"
          options={itmOpts}
          selectedIds={value.itemIds}
          onChange={(ids) => onChange({ ...value, itemIds: ids })}
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

      <div className="refgroup tasks-group">
        <label>Tasks ({value.tasks.length})</label>
        {value.tasks.map((t, idx) => (
          <TaskCard
            key={t.id}
            value={t}
            permissions={adventure.permissions}
            cutScenes={adventure.cutScenes}
            onChange={(u) => updateTask(idx, u)}
            onDelete={() => removeTask(idx)}
          />
        ))}
        <button onClick={addTask}>+ Task</button>
      </div>
    </EntityCard>
  );
}
