import { useState, type ReactNode } from 'react';

type Props = {
  id: string;
  title: string;
  subtitle?: string;
  extras?: ReactNode;
  defaultOpen?: boolean;
  className?: string;
  deleteLabel?: string;
  onDelete: () => void;
  children: ReactNode;
};

export function EntityCard({
  id,
  title,
  subtitle,
  extras,
  defaultOpen,
  className,
  deleteLabel = 'löschen',
  onDelete,
  children,
}: Props) {
  const [open, setOpen] = useState(defaultOpen ?? title.trim() === '');
  const cls = ['entity', open ? 'open' : 'collapsed', className].filter(Boolean).join(' ');

  return (
    <div className={cls}>
      <div className="entity-header" onClick={() => setOpen((o) => !o)}>
        <span className="toggle">{open ? '▾' : '▸'}</span>
        <span className="title">
          {title.trim() ? title : <em className="placeholder">unbenannt</em>}
        </span>
        {subtitle && <span className="subtitle">{subtitle}</span>}
        <span className="id">{id}</span>
        {extras && (
          <span className="extras" onClick={(e) => e.stopPropagation()}>
            {extras}
          </span>
        )}
        <button
          className="danger"
          onClick={(e) => {
            e.stopPropagation();
            onDelete();
          }}
        >
          {deleteLabel}
        </button>
      </div>
      {open && <div className="entity-body">{children}</div>}
    </div>
  );
}
