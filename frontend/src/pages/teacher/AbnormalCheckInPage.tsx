import { DataTable } from "../../shared/components/DataTable";
import { StatusBadge } from "../../shared/components/StatusBadge";

const rows = [
  { student: "王同学", date: "2026-06-15", reason: "配速异常", status: "待核验" },
  { student: "赵同学", date: "2026-06-14", reason: "负数距离", status: "数据异常" }
];

export function AbnormalCheckInPage() {
  return (
    <section className="panel">
      <div className="section-heading">
        <h2>异常打卡</h2>
        <span>教师可标记、备注或修正，不删除原始记录</span>
      </div>
      <DataTable
        rows={rows}
        columns={[
          { key: "student", header: "学生", render: (row) => row.student },
          { key: "date", header: "日期", render: (row) => row.date },
          { key: "reason", header: "原因", render: (row) => row.reason },
          { key: "status", header: "状态", render: (row) => <StatusBadge tone="warning">{row.status}</StatusBadge> }
        ]}
      />
    </section>
  );
}

