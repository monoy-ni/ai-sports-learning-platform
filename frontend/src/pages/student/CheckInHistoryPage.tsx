import { DataTable } from "../../shared/components/DataTable";
import { StatusBadge } from "../../shared/components/StatusBadge";

const rows = [
  { date: "2026-06-16", type: "跑步", duration: "30 分钟", status: "正常" },
  { date: "2026-06-15", type: "室内无器械", duration: "24 分钟", status: "待核验" }
];

export function CheckInHistoryPage() {
  return (
    <section className="panel">
      <div className="section-heading">
        <h2>历史打卡</h2>
        <span>保留原始记录和异常标记</span>
      </div>
      <DataTable
        rows={rows}
        columns={[
          { key: "date", header: "日期", render: (row) => row.date },
          { key: "type", header: "运动类型", render: (row) => row.type },
          { key: "duration", header: "时长", render: (row) => row.duration },
          { key: "status", header: "状态", render: (row) => <StatusBadge tone={row.status === "正常" ? "success" : "warning"}>{row.status}</StatusBadge> }
        ]}
      />
    </section>
  );
}

