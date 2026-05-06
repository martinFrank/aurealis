type Option = { id: string; label: string };

type Props = {
  label: string;
  options: Option[];
  selectedIds: string[];
  onChange: (ids: string[]) => void;
};

export function RefMultiSelect({ label, options, selectedIds, onChange }: Props) {
  const toggle = (id: string, checked: boolean) => {
    if (checked) {
      if (selectedIds.includes(id)) return;
      onChange([...selectedIds, id]);
    } else {
      onChange(selectedIds.filter((s) => s !== id));
    }
  };

  return (
    <div className="refgroup">
      <label>{label}</label>
      {options.length === 0 ? (
        <div className="empty">noch keine Einträge auf Adventure-Ebene</div>
      ) : (
        <ul className="checklist">
          {options.map((o) => (
            <li key={o.id}>
              <label>
                <input
                  type="checkbox"
                  checked={selectedIds.includes(o.id)}
                  onChange={(e) => toggle(o.id, e.target.checked)}
                />
                <span>{o.label || o.id}</span>
              </label>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

type SingleProps = {
  label: string;
  options: Option[];
  selectedId: string;
  onChange: (id: string) => void;
};

export function RefSelect({ label, options, selectedId, onChange }: SingleProps) {
  return (
    <div>
      <label>{label}</label>
      <select value={selectedId} onChange={(e) => onChange(e.target.value)}>
        <option value="">— bitte wählen —</option>
        {options.map((o) => (
          <option key={o.id} value={o.id}>
            {o.label || o.id}
          </option>
        ))}
      </select>
    </div>
  );
}
