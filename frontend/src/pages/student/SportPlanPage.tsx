import { ClipboardList, RotateCcw } from "lucide-react";
import { StatusBadge } from "../../shared/components/StatusBadge";

export function SportPlanPage() {
  return (
    <section className="panel">
      <div className="section-heading">
        <h2>AI 学期运动计划</h2>
        <StatusBadge tone="success">已生成</StatusBadge>
      </div>
      <div className="timeline">
        <div><strong>第 1-2 周：</strong>适应性慢跑，建立心肺基础，配速控制在舒适区间。</div>
        <div><strong>第 3-5 周：</strong>加入间歇跑和节奏跑，逐步提升跑步经济性。</div>
        <div><strong>第 6-8 周：</strong>Léger/折返跑训练，强化校园跑应试能力。</div>
        <div><strong>雨天替代：</strong>原地高抬腿、开合跳、核心训练、动态拉伸。</div>
      </div>
      <div className="button-row">
        <button className="primary-button" type="button"><ClipboardList size={18} />生成计划</button>
        <button className="secondary-button" type="button"><RotateCcw size={18} />申请重置</button>
      </div>
    </section>
  );
}

