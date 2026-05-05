import { useState, type ReactNode } from 'react';

type Props = {
  title: string;
  count?: number;
  defaultOpen?: boolean;
  children: ReactNode;
};

export function Section({ title, count, defaultOpen = true, children }: Props) {
  const [open, setOpen] = useState(defaultOpen);
  return (
    <div className="section">
      <header onClick={() => setOpen((o) => !o)}>
        <h2>{title}</h2>
        {count !== undefined && <span className="count">{count}</span>}
        <span>{open ? '▾' : '▸'}</span>
      </header>
      {open && <div className="body">{children}</div>}
    </div>
  );
}
