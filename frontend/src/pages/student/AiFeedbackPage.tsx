import { Image, Sparkles } from "lucide-react";
import { StatusBadge } from "../../shared/components/StatusBadge";

export function AiFeedbackPage() {
  return (
    <div className="page-grid two-columns">
      <section className="panel">
        <div className="section-heading">
          <h2>AI 运动建议</h2>
          <StatusBadge tone="success">SUCCESS</StatusBadge>
        </div>
        <p>本次训练完成度较好，配速稳定。建议运动后进行小腿后侧、髂腰肌和臀部拉伸，每个动作保持 20-30 秒。</p>
        <p>如果出现胸闷、头晕或持续疼痛，应停止运动并咨询教师或医生。</p>
      </section>
      <section className="panel visual-card">
        <Image size={44} />
        <h2>每日运动卡片</h2>
        <StatusBadge tone="info">mock 图片任务</StatusBadge>
        <span>生图服务接入后，这里展示当日专属视觉记录。</span>
        <Sparkles size={24} />
      </section>
    </div>
  );
}

