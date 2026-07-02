import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { EmptyState } from "../../shared/components/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { getStudentDetail, getStudentNote, updateStudentNote, StudentDetail } from "../../shared/api/teacherApi";
import { StatusTone } from "../../shared/api/types";

const RISK_TONE: Record<string, StatusTone> = { danger: "danger", warning: "warning", info: "info" };

function TrendBars({ points }: { points: Array<{ date: string; durationMinutes: number | null }> }) {
  const max = Math.max(1, ...points.map((p) => p.durationMinutes ?? 0));
  if (points.length === 0) return <EmptyState title="暂无趋势" />;
  return (
    <div className="trend-bars">
      {points.map((p) => (
        <div key={p.date} className="bar" title={`${p.date}: ${p.durationMinutes ?? 0}分钟`}
          style={{ height: `${((p.durationMinutes ?? 0) / max) * 100}%` }} />
      ))}
    </div>
  );
}

export function StudentDetailPage() {
  const { studentId } = useParams<{ studentId: string }>();
  const sid = Number(studentId);
  const [data, setData] = useState<StudentDetail | null>(null);
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    setLoading(true);
    setError(null);
    getStudentDetail(sid)
      .then((d) => { setData(d); setNote(d.teacherNote ?? ""); })
      .catch((e) => setError(e instanceof Error ? e.message : "加载失败"))
      .finally(() => setLoading(false));
  }, [sid]);

  async function saveNote() {
    setSaving(true);
    try {
      await updateStudentNote(sid, data?.healthProfile ? 0 : 0, note);
    } catch {
      // ignore for MVP
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <LoadingState label="加载学生详情" />;
  if (error) return <ErrorState message={error} />;
  if (!data) return <EmptyState title="未找到学生" />;

  const hp = data.healthProfile;
  const maxDur = Math.max(1, ...data.healthTrend.points.map((p) => p.durationMinutes ?? 0));

  return (
    <div className="page-grid two-columns">
      <div>
        <div className="panel">
          <h3 className="section-heading">学生档案</h3>
          <dl className="kv">
            <dt>姓名</dt><dd>{data.name}</dd>
            <dt>学号</dt><dd>{data.studentNumber}</dd>
            <dt>班级</dt><dd>{data.className || "—"}</dd>
            {hp && <>
              <dt>性别/年龄</dt><dd>{hp.gender} / {hp.age}</dd>
              <dt>身高/体重</dt><dd>{hp.heightCm}cm / {hp.weightKg}kg</dd>
              <dt>BMI</dt><dd>{hp.bmi}</dd>
              <dt>肺活量</dt><dd>{hp.vitalCapacity}</dd>
              <dt>疾病</dt><dd>{hp.diseaseStatus}{hp.diseaseNote ? `（${hp.diseaseNote}）` : ""}</dd>
              <dt>运动目标</dt><dd>{hp.sportGoal}</dd>
              <dt>每周频次</dt><dd>{hp.weeklyFrequency}</dd>
              <dt>体型</dt><dd>{hp.bodyType}</dd>
            </>}
          </dl>
        </div>

        {data.sportPlan && (
          <div className="panel section-gap">
            <h3 className="section-heading">运动计划（{data.sportPlan.generationStatus}）</h3>
            <pre className="ai-original">{data.sportPlan.weeklyPlan}</pre>
          </div>
        )}

        <div className="panel section-gap">
          <h3 className="section-heading">校园跑分数</h3>
          <p style={{ fontSize: 28, color: "var(--color-primary)", margin: 0 }}>
            {data.campusRunScore.score} <span style={{ fontSize: 13, color: "var(--color-muted)" }}>来源: {data.campusRunScore.dataSource}</span>
          </p>
        </div>

        <div className="panel section-gap">
          <h3 className="section-heading">健康数据趋势（近30天）</h3>
          <TrendBars points={data.healthTrend.points} />
          <p style={{ fontSize: 12, color: "var(--color-muted)", marginTop: 6 }}>最高单次 {maxDur} 分钟</p>
        </div>

        <div className="panel section-gap">
          <h3 className="section-heading">AI 每日建议</h3>
          {data.aiDailySuggestion.imageUrl && <img src={data.aiDailySuggestion.imageUrl} alt="每日形象" style={{ maxWidth: "100%", borderRadius: 6, marginBottom: 8 }} />}
          <p>{data.aiDailySuggestion.summary}</p>
          <ul>{data.aiDailySuggestion.suggestions.map((s, i) => <li key={i}>{s}</li>)}</ul>
        </div>
      </div>

      <div>
        <div className="panel">
          <h3 className="section-heading">风险标记</h3>
          {data.riskFlags.length === 0 ? <EmptyState title="暂无风险点" /> : (
            <div className="timeline">
              {data.riskFlags.map((f) => (
                <div key={f.type} className="timeline-item">
                  <StatusBadge tone={RISK_TONE[f.severity] ?? "neutral"}>{f.type}</StatusBadge>
                  <div style={{ marginTop: 4 }}>{f.label}</div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="panel section-gap">
          <h3 className="section-heading">近期打卡记录</h3>
          {data.recentCheckIns.length === 0 ? <EmptyState title="暂无打卡" /> : (
            <div className="timeline">
              {data.recentCheckIns.map((c) => (
                <div key={c.id} className="timeline-item">
                  <div className="meta">{c.date} · {c.exerciseType} · {c.durationMinutes}分钟</div>
                  {c.abnormal && <StatusBadge tone="danger">异常: {c.abnormalReason ?? ""}</StatusBadge>}
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="panel section-gap">
          <h3 className="section-heading">教师备注</h3>
          <textarea className="editor" aria-label="教师备注" placeholder="填写学生备注…" value={note} onChange={(e) => setNote(e.target.value)} />
          <div className="button-row" style={{ marginTop: 8 }}>
            <button type="button" className="primary-button" onClick={saveNote} disabled={saving}>{saving ? "保存中…" : "保存备注"}</button>
          </div>
        </div>
      </div>
    </div>
  );
}
