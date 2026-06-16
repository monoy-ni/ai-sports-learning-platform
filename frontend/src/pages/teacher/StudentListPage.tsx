import { DataTable } from "../../shared/components/DataTable";
import { StatusBadge } from "../../shared/components/StatusBadge";

const rows = [
  { id: 1, name: "李同学", className: "一班", bmi: "21.8", status: "正常" },
  { id: 2, name: "王同学", className: "一班", bmi: "27.2", status: "关注" }
];

export function StudentListPage() {
  return (
    <section className="panel">
      <div className="section-heading">
        <h2>学生列表</h2>
        <span>支持后续按班级、BMI、打卡状态、报告状态筛选</span>
      </div>
      <DataTable
        rows={rows}
        columns={[
          { key: "name", header: "姓名", render: (row) => row.name },
          { key: "className", header: "班级", render: (row) => row.className },
          { key: "bmi", header: "BMI", render: (row) => row.bmi },
          { key: "status", header: "状态", render: (row) => <StatusBadge tone={row.status === "正常" ? "success" : "warning"}>{row.status}</StatusBadge> }
        ]}
      />
    </section>
  );
}

