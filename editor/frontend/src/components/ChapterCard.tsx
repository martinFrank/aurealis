import type { Adventure, Chapter, Task } from '../types';
import { newTask } from '../factories';
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
  const perOpts = adventure.persons.map((p) => ({ id: p.id, label: p.name }));
  const itmOpts = adventure.items.map((i) => ({ id: i.id, label: i.name }));

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

  return (
    <div className="entity">
      <div className="entity-header">
        <span className="id">{value.id}</span>
        <button className="danger" onClick={onDelete}>
          Chapter löschen
        </button>
      </div>
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
          label="Persons"
          options={perOpts}
          selectedIds={value.personIds}
          onChange={(ids) => onChange({ ...value, personIds: ids })}
        />
      </div>
      <div className="row">
        <RefMultiSelect
          label="Items"
          options={itmOpts}
          selectedIds={value.itemIds}
          onChange={(ids) => onChange({ ...value, itemIds: ids })}
        />
        <div />
      </div>

      <div className="refgroup" style={{ marginTop: '0.8rem' }}>
        <label>Tasks ({value.tasks.length})</label>
        {value.tasks.map((t, idx) => (
          <TaskCard
            key={t.id}
            value={t}
            permissions={adventure.permissions}
            onChange={(u) => updateTask(idx, u)}
            onDelete={() => removeTask(idx)}
          />
        ))}
        <button onClick={addTask}>+ Task</button>
      </div>
    </div>
  );
}
