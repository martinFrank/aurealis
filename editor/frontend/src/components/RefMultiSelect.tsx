type Option = { id: string; label: string };

type Props = {
  label: string;
  options: Option[];
  selectedIds: string[];
  onChange: (ids: string[]) => void;
};

export function RefMultiSelect({ label, options, selectedIds, onChange }: Props) {
  return (
    <div className="refgroup">
      <label>{label}</label>
      {options.length === 0 ? (
        <div className="empty">noch keine Einträge auf Adventure-Ebene</div>
      ) : (
        <select
          multiple
          value={selectedIds}
          onChange={(e) =>
            onChange(Array.from(e.target.selectedOptions).map((o) => o.value))
          }
        >
          {options.map((o) => (
            <option key={o.id} value={o.id}>
              {o.label || o.id}
            </option>
          ))}
        </select>
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
