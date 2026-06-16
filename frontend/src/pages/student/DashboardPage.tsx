import { CalendarCheck, Footprints, HeartPulse, Sparkles } from "lucide-react";
import { StatusBadge } from "../../shared/components/StatusBadge";

const metrics = [
  { label: "本周打卡", value: "3/5", helper: "计划完成率 60%", icon: CalendarCheck },
  { label: "校园跑分", value: "82", helper: "较上周 +4", icon: Footprints },
  { label: "BMI", value: "21.8", helper: "正常范围", icon: HeartPulse },
  { label: "AI 任务", value: "正常", helper: "今日建议已生成", icon: Sparkles }
];

export function DashboardPage() {
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
          <h2>本周计划</h2>
          <StatusBadge tone="info">第 4 周</StatusBadge>
        </div>
        <div className="timeline">
          <div>普通跑 2 次，每次 25-30 分钟，配速 6'30"-7'10"/km。</div>
          <div>节奏跑 1 次，含 8 分钟热身和 6 分钟拉伸。</div>
          <div>雨天切换为低冲击有氧、核心训练和动态拉伸。</div>
        </div>
      </section>
      <section className="panel accent">
        <div className="section-heading">
          <h2>今日建议</h2>
          <StatusBadge tone="success">AI 已生成</StatusBadge>
        </div>
        <p>今天适合轻中强度跑步。若疲劳程度超过 7 分，建议降低配速并增加拉伸恢复时间。该建议不能替代医学诊断。</p>
      </section>
    </div>
  );
}

