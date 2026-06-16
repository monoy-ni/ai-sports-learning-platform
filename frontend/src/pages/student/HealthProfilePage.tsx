import { Save } from "lucide-react";

export function HealthProfilePage() {
  return (
    <section className="panel">
      <div className="section-heading">
        <h2>首次健康建档</h2>
        <span>系统将根据身高体重自动计算 BMI</span>
      </div>
      <div className="form-grid two">
        <label>性别<input defaultValue="男" /></label>
        <label>年龄<input type="number" defaultValue={19} /></label>
        <label>身高 cm<input type="number" defaultValue={175} /></label>
        <label>体重 kg<input type="number" defaultValue={67} /></label>
        <label>肺活量 ml<input type="number" defaultValue={4200} /></label>
        <label>每周运动频率<input type="number" defaultValue={4} /></label>
        <label>健康疾病状态<select defaultValue="NONE"><option value="NONE">无</option><option value="HAS_DISEASE">有</option><option value="UNKNOWN">不确定</option></select></label>
        <label>运动目标<input defaultValue="提升校园跑成绩" /></label>
      </div>
      <button className="primary-button" type="button"><Save size={18} />保存建档</button>
    </section>
  );
}

