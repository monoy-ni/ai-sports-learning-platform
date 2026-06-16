import { AlertTriangle, BarChart3, CheckCircle2, Users } from "lucide-react";
import { StatusBadge } from "../../shared/components/StatusBadge";

const metrics = [
  { label: "学生总数", value: "128", helper: "3 个班级", icon: Users },
  { label: "已建档", value: "116", helper: "完成率 90.6%", icon: CheckCircle2 },
  { label: "异常数据", value: "7", helper: "需人工关注", icon: AlertTriangle },
  { label: "平均分", value: "78.4", helper: "校园跑", icon: BarChart3 }
];

export function TeacherDashboardPage() {
  return (
    <div className="page-grid">
      <section className="metric-grid">
        {metrics.map((metric) => {
          const Icon = metric.icon;
          return (
            <article className="metric-card" key={metric.label}>
              <Icon size={22} />
              <span>{metric.label}</span>
              <strong>{metric.value}</strong>
              <small>{metric.helper}</small>
            </article>
          );
        })}
      </section>
      <section className="panel">
        <div className="section-heading">
          <h2>风险关注</h2>
          <StatusBadge tone="warning">7 条</StatusBadge>
        </div>
        <div className="timeline">
          <div>连续缺卡学生：3 人</div>
          <div>BMI 异常且运动量偏低：2 人</div>
          <div>配速数据异常：2 条记录</div>
        </div>
      </section>
    </div>
  );
}

