import { Send } from "lucide-react";

export function CheckInPage() {
  return (
    <section className="panel">
      <div className="section-heading">
        <h2>每日运动打卡</h2>
        <span>每日仅允许提交一次</span>
      </div>
      <div className="form-grid two">
        <label>日期<input type="date" /></label>
        <label>天气<select defaultValue="SUNNY"><option value="SUNNY">晴天</option><option value="RAINY">雨天</option><option value="CLOUDY">阴天</option></select></label>
        <label>运动类型<select defaultValue="RUNNING"><option value="RUNNING">跑步</option><option value="INDOOR">室内无器械</option></select></label>
        <label>时长 分钟<input type="number" defaultValue={30} /></label>
        <label>距离 km<input type="number" defaultValue={3.2} /></label>
        <label>主观疲劳 1-10<input type="number" defaultValue={5} /></label>
      </div>
      <label className="full-field">运动感受<textarea defaultValue="今天状态稳定，后半程略有疲劳。" /></label>
      <button className="primary-button" type="button"><Send size={18} />提交打卡并生成 AI 建议</button>
    </section>
  );
}

