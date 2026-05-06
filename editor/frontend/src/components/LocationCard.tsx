import type { Adventure, LocalizedPerson, Location } from '../types';
import { newLocalizedPerson } from '../factories';
import { RefMultiSelect, RefSelect } from './RefMultiSelect';

type Props = {
  value: Location;
  adventure: Adventure;
  onChange: (v: Location) => void;
  onDelete: () => void;
};

export function LocationCard({ value, adventure, onChange, onDelete }: Props) {
  const permOpts = adventure.permissions.map((p) => ({ id: p.id, label: p.name }));
  const personOpts = adventure.persons.map((p) => ({ id: p.id, label: p.name }));

  const updateLp = (idx: number, lp: LocalizedPerson) => {
    const persons = value.persons.slice();
    persons[idx] = lp;
    onChange({ ...value, persons });
  };

  const removeLp = (idx: number) => {
    const persons = value.persons.slice();
    persons.splice(idx, 1);
    onChange({ ...value, persons });
  };

  const addLp = () => {
    onChange({ ...value, persons: [...value.persons, newLocalizedPerson()] });
  };

  return (
    <div className="entity">
      <div className="entity-header">
        <span className="id">{value.id}</span>
        <button className="danger" onClick={onDelete}>
          löschen
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
          <label>AI Hints</label>
          <input
            value={value.aiHints}
            onChange={(e) => onChange({ ...value, aiHints: e.target.value })}
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
      <div className="row full">
        <RefMultiSelect
          label="Required Permissions (Zutritt)"
          options={permOpts}
          selectedIds={value.requiredPermissionIds}
          onChange={(ids) => onChange({ ...value, requiredPermissionIds: ids })}
        />
      </div>

      <div className="refgroup">
        <label>Localized Persons ({value.persons.length})</label>
        {value.persons.map((lp, idx) => (
          <div className="entity" key={idx} style={{ marginBottom: '0.4rem' }}>
            <div className="row">
              <RefSelect
                label="Person"
                options={personOpts}
                selectedId={lp.personId}
                onChange={(id) => updateLp(idx, { ...lp, personId: id })}
              />
              <RefSelect
                label="Required Permission"
                options={permOpts}
                selectedId={lp.requiredPermissionId}
                onChange={(id) => updateLp(idx, { ...lp, requiredPermissionId: id })}
              />
            </div>
            <button className="danger" onClick={() => removeLp(idx)}>
              entfernen
            </button>
          </div>
        ))}
        <button onClick={addLp}>+ Localized Person</button>
      </div>
    </div>
  );
}
