import { AlertTriangle, BarChart3, RefreshCw } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { EmptyState } from "../../shared/components/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { getClasses, getTeacherDashboard, getTerms, SchoolClass, TeacherDashboard, Term } from "../../shared/api/teacherApi";

function Distribution({ rows }: { rows: Array<{ label: string; count: number; tone?: string }> }) {
  const max = Math.max(1, ...rows.map((r) => r.count));
  return (
    <div>
      {rows.map((r) => (
        <div key={r.label} className="distribution-row">
          <span className="label">{r.label}</span>
          <span className="bar">
            <span style={{ width: `${(r.count / max) * 100}%` }} />
          </span>
          <span className="count">{r.count}</span>
        </div>
      ))}
    </div>
  );
}

function RiskList({ title, students }: { title: string; students: Array<{ id: number; name: string; reason: string; status: string }> }) {
  return (
    <div className="panel">
      <h3 className="section-heading">{title}（{students.length}）</h3>
      {students.length === 0 ? (
        <EmptyState title="暂无" description="该类风险学生为空" />
      ) : (
        <div className="timeline">
          {students.map((s) => (
            <div key={s.id} className="timeline-item">
              <div className="meta">{s.name}</div>
              <div>{s.reason}</div>
              <StatusBadge tone="warning">{s.status}</StatusBadge>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export function TeacherDashboardPage() {
  const navigate = useNavigate();
  const [terms, setTerms] = useState<Term[]>([]);
  const [classes, setClasses] = useState<SchoolClass[]>([]);
  const [termId, setTermId] = useState<number | undefined>();
  const [classId, setClassId] = useState<number | undefined>();
  const [data, setData] = useState<TeacherDashboard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      const d = await getTeacherDashboard(classId, termId);
      setData(d);
    } catch (e) {
      setError(e instanceof Error ? e.message : "加载失败");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    getTerms().then(setTerms).catch(() => {});
  }, []);
  useEffect(() => {
    getClasses(termId).then(setClasses).catch(() => {});
  }, [termId]);
  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [termId, classId]);

  return (
    <div>
      <div className="filter-bar">
        <label>
          学期
          <select value={termId ?? ""} onChange={(e) => setTermId(e.target.value ? Number(e.target.value) : undefined)}>
            <option value="">当前学期</option>
            {terms.map((t) => (
              <option key={t.id} value={t.id}>{t.name}</option>
            ))}
          </select>
        </label>
        <label>
          班级
          <select value={classId ?? ""} onChange={(e) => setClassId(e.target.value ? Number(e.target.value) : undefined)}>
            <option value="">全部班级</option>
            {classes.map((c) => (
              <option key={c.id} value={c.id}>{c.className}</option>
            ))}
          </select>
        </label>
        <button type="button" className="ghost-button" onClick={load}><RefreshCw size={16} />刷新</button>
      </div>

      {loading && <LoadingState label="加载看板" />}
      {error && <ErrorState message={error} />}
      {!loading && data && (
        <>
          <div className="metric-grid">
            {data.metrics.map((m) => (
              <div key={m.label} className="metric-card">
                <span className="value">{m.value}</span>
                <span className="label">{m.label}</span>
                <span className="helper">{m.helper}</span>
              </div>
            ))}
          </div>

          <div className="page-grid two-columns section-gap">
            <div className="panel">
              <h3 className="section-heading"><BarChart3 size={16} /> 运动记录看板</h3>
              <dl className="kv">
                <dt>打卡次数</dt><dd>{data.exercise.totalCheckIns}</dd>
                <dt>平均时长</dt><dd>{data.exercise.avgDurationMinutes} 分钟</dd>
                <dt>平均距离</dt><dd>{data.exercise.avgDistanceKm} km</dd>
                <dt>平均配速</dt><dd>{data.exercise.avgPaceMinutesPerKm} min/km</dd>
                <dt>计划完成率</dt><dd>{(data.exercise.planCompletionRate * 100).toFixed(0)}%</dd>
              </dl>
            </div>
            <div className="panel">
              <h3 className="section-heading">BMI 分布</h3>
              <Distribution rows={[
                { label: "偏瘦", count: data.bmiDistribution.underweight },
                { label: "正常", count: data.bmiDistribution.normal },
                { label: "超重", count: data.bmiDistribution.overweight },
                { label: "肥胖", count: data.bmiDistribution.obese },
              ]} />
              <h3 className="section-heading" style={{ marginTop: 14 }}>肺活量分布</h3>
              <Distribution rows={[
                { label: "优秀", count: data.vitalCapacity.excellent },
                { label: "及格", count: data.vitalCapacity.pass },
                { label: "偏低", count: data.vitalCapacity.fail },
              ]} />
            </div>
          </div>

          <div className="risk-grid section-gap">
            <RiskList title="连续缺卡学生" students={data.consecutiveMissStudents} />
            <RiskList title="运动量过低学生" students={data.lowVolumeStudents} />
            <RiskList title="数据异常学生" students={data.abnormalDataStudents} />
            <RiskList title="健康风险学生" students={data.healthRiskStudents} />
          </div>

          <div className="panel section-gap">
            <h3 className="section-heading"><AlertTriangle size={16} /> 风险学生名单</h3>
            <button type="button" className="secondary-button" onClick={() => navigate("/teacher/students")}>前往学生列表处理</button>
          </div>
        </>
      )}
    </div>
  );
}
