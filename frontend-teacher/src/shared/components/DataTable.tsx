import { useMemo, useState } from "react";

export type Column<T> = {
  key: string;
  header: string;
  render: (row: T) => React.ReactNode;
  /** 返回排序键, 不提供则该列不可排序 */
  sortValue?: (row: T) => string | number | null;
};

type DataTableProps<T> = {
  columns: Column<T>[];
  rows: T[];
  rowKey?: (row: T, index: number) => string | number;
  onRowClick?: (row: T) => void;
};

export function DataTable<T>({ columns, rows, rowKey, onRowClick }: DataTableProps<T>) {
  const [sortKey, setSortKey] = useState<string | null>(null);
  const [sortDir, setSortDir] = useState<"asc" | "desc">("asc");

  const sortableColumns = columns.filter((c) => c.sortValue);

  const sortedRows = useMemo(() => {
    if (!sortKey) return rows;
    const col = columns.find((c) => c.key === sortKey);
    if (!col || !col.sortValue) return rows;
    const copy = [...rows];
    copy.sort((a, b) => {
      const va = col.sortValue!(a);
      const vb = col.sortValue!(b);
      if (va === null || va === undefined) return 1;
      if (vb === null || vb === undefined) return -1;
      if (va < vb) return sortDir === "asc" ? -1 : 1;
      if (va > vb) return sortDir === "asc" ? 1 : -1;
      return 0;
    });
    return copy;
  }, [rows, sortKey, sortDir, columns]);

  function toggleSort(key: string) {
    if (sortKey === key) {
      setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortKey(key);
      setSortDir("asc");
    }
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((column) => {
              const sortable = sortableColumns.includes(column);
              const active = sortKey === column.key;
              return (
                <th
                  key={column.key}
                  className={sortable ? "th-sortable" : undefined}
                  onClick={sortable ? () => toggleSort(column.key) : undefined}
                >
                  {column.header}
                  {sortable && <span className="sort-arrow">{active ? (sortDir === "asc" ? " ▲" : " ▼") : " ↕"}</span>}
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody>
          {sortedRows.map((row, rowIndex) => (
            <tr
              key={rowKey ? rowKey(row, rowIndex) : rowIndex}
              className={onRowClick ? "tr-clickable" : undefined}
              onClick={onRowClick ? () => onRowClick(row) : undefined}
            >
              {columns.map((column) => (
                <td key={column.key}>{column.render(row)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
