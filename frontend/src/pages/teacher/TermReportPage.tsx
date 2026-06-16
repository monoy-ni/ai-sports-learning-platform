import { FileText } from "lucide-react";
import { StatusBadge } from "../../shared/components/StatusBadge";

export function TermReportPage() {
  return (
    <section className="panel">
      <div className="section-heading">
        <h2>结课报告</h2>
        <StatusBadge tone="info">草稿待审核</StatusBadge>
      </div>
      <p>AI 报告包含基础健康概况、运动参与、计划执行、校园跑成绩、健康数据变化、风险与不足、综合评价和后续建议。</p>
      <textarea defaultValue="该学生本学期运动参与整体稳定，建议继续保持每周 3-4 次中等强度运动。" />
      <div className="button-row">
        <button className="primary-button" type="button"><FileText size={18} />生成报告草稿</button>
        <button className="secondary-button" type="button">审核通过</button>
      </div>
    </section>
  );
}

