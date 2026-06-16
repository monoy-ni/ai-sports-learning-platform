import { StatusBadge } from "../../shared/components/StatusBadge";

export function StudentDetailPage() {
  return (
    <div className="page-grid two-columns">
      <section className="panel">
        <div className="section-heading">
          <h2>学生详情</h2>
          <StatusBadge tone="warning">存在风险点</StatusBadge>
        </div>
        <div className="timeline">
          <div>BMI：27.2，建议关注体重变化。</div>
          <div>近 7 天缺卡 3 次。</div>
          <div>最近一次配速明显不合理，已标记待核验。</div>
        </div>
      </section>
      <section className="panel">
        <div className="section-heading">
          <h2>教师备注</h2>
        </div>
        <textarea defaultValue="提醒学生降低强度，优先保持规律参与。" />
      </section>
    </div>
  );
}

