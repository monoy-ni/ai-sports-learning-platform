import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Column, DataTable } from "../../shared/components/DataTable";
import { EmptyState } from "../../shared/components/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { getClasses, getTerms, listStudents, SchoolClass, StudentListItem, StudentListQuery, Term } from "../../shared/api/teacherApi";
import { StatusTone } from "../../shared/api/types";

const BMI_TONES: Record<string, StatusTone> = {
  正常: "success", 偏瘦: "info", 超重: "warning", 肥胖: "danger"
};
const CHECKIN_TONES: Record<string, StatusTone> = {
  ACTIVE: "success", MISSED: "danger", LOW_VOLUME: "warning"
};
const CHECKIN_LABEL: Record<string, string> = {
  ACTIVE: "活跃", MISSED: "连续缺卡", LOW_VOLUME: "运动量低"
};
const REPORT_TONES: Record<string, StatusTone> = {
  DRAFT: "warning", APPROVED: "success", NONE: "neutral"
};
const REPORT_LABEL: Record<string, string> = {
  DRAFT: "草稿", APPROVED: "已审核", NONE: "未生成"
};

export function StudentListPage() {
  const navigate = useNavigate();
  const [terms, setTerms] = useState<Term[]>([]);
  const [classes, setClasses] = useState<SchoolClass[]>([]);
  const [query, setQuery] = useState<StudentListQuery>({});
  const [rows, setRows] = useState<StudentListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getTerms().then(setTerms).catch(() => {});
    getClasses().then(setClasses).catch(() => {});
  }, []);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      setRows(await listStudents(query));
    } catch (e) {
      setError(e instanceof Error ? e.message : "加载失败");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [query]);

  const columns: Column<StudentListItem>[] = [
    { key: "name", header: "姓名", render: (r) => r.name, sortValue: (r) => r.name },
    { key: "studentNumber", header: "学号", render: (r) => r.studentNumber, sortValue: (r) => r.studentNumber },
    { key: "className", header: "班级", render: (r) => r.className },
    {
      key: "bmi", header: "BMI",
      render: (r) => r.bmi ? `${r.bmi} (${r.bmiCategory})` : "—",
      sortValue: (r) => r.bmi
    },
    {
      key: "campusRunScore", header: "校园跑分",
      render: (r) => r.campusRunScore ?? "—",
      sortValue: (r) => r.campusRunScore
    },
    {
      key: "checkInStatus", header: "打卡状态",
      render: (r) => <StatusBadge tone={CHECKIN_TONES[r.checkInStatus] ?? "neutral"}>{CHECKIN_LABEL[r.checkInStatus] ?? r.checkInStatus}</StatusBadge>
    },
    {
      key: "reportStatus", header: "报告状态",
      render: (r) => <StatusBadge tone={REPORT_TONES[r.reportStatus] ?? "neutral"}>{REPORT_LABEL[r.reportStatus] ?? r.reportStatus}</StatusBadge>
    },
    {
      key: "riskFlagCount", header: "风险点",
      render: (r) => r.riskFlagCount > 0 ? <StatusBadge tone="danger">{r.riskFlagCount}</StatusBadge> : "—",
      sortValue: (r) => r.riskFlagCount
    },
  ];

  function set<K extends keyof StudentListQuery>(key: K, value: StudentListQuery[K]) {
    setQuery((q) => ({ ...q, [key]: value }));
  }

  return (
    <div>
      <div className="filter-bar">
        <label>搜索<input value={query.q ?? ""} onChange={(e) => set("q", e.target.value)} placeholder="姓名/学号" /></label>
        <label>班级
          <select value={query.classId ?? ""} onChange={(e) => set("classId", e.target.value ? Number(e.target.value) : undefined)}>
            <option value="">全部</option>
            {classes.map((c) => <option key={c.id} value={c.id}>{c.className}</option>)}
          </select>
        </label>
        <label>BMI 分类
          <select value={query.bmiCategory ?? ""} onChange={(e) => set("bmiCategory", e.target.value || undefined)}>
            <option value="">全部</option>
            <option value="偏瘦">偏瘦</option>
            <option value="正常">正常</option>
            <option value="超重">超重</option>
            <option value="肥胖">肥胖</option>
          </select>
        </label>
        <label>分数起<input type="number" value={query.scoreMin ?? ""} onChange={(e) => set("scoreMin", e.target.value ? Number(e.target.value) : undefined)} /></label>
        <label>分数止<input type="number" value={query.scoreMax ?? ""} onChange={(e) => set("scoreMax", e.target.value ? Number(e.target.value) : undefined)} /></label>
        <label>打卡状态
          <select value={query.checkInStatus ?? ""} onChange={(e) => set("checkInStatus", e.target.value || undefined)}>
            <option value="">全部</option>
            <option value="ACTIVE">活跃</option>
            <option value="MISSED">连续缺卡</option>
            <option value="LOW_VOLUME">运动量低</option>
          </select>
        </label>
        <label>报告状态
          <select value={query.reportStatus ?? ""} onChange={(e) => set("reportStatus", e.target.value || undefined)}>
            <option value="">全部</option>
            <option value="DRAFT">草稿</option>
            <option value="APPROVED">已审核</option>
            <option value="NONE">未生成</option>
          </select>
        </label>
        <label>健康风险
          <select value={query.healthRisk ? "1" : ""} onChange={(e) => set("healthRisk", e.target.value === "1")}>
            <option value="">全部</option>
            <option value="1">仅风险</option>
          </select>
        </label>
        <label>排序
          <select value={query.sort ?? ""} onChange={(e) => set("sort", e.target.value || undefined)}>
            <option value="">默认</option>
            <option value="NAME">姓名</option>
            <option value="STUDENT_NUMBER">学号</option>
            <option value="BMI">BMI</option>
            <option value="SCORE">校园跑分</option>
          </select>
        </label>
        <label>方向
          <select value={query.dir ?? "ASC"} onChange={(e) => set("dir", e.target.value)}>
            <option value="ASC">升序</option>
            <option value="DESC">降序</option>
          </select>
        </label>
        <button type="button" className="ghost-button" onClick={load}>刷新</button>
      </div>

      {loading && <LoadingState label="加载学生列表" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && (
        rows.length === 0 ? <EmptyState title="暂无学生" description="调整筛选条件或确认班级数据" /> :
        <DataTable
          columns={columns}
          rows={rows}
          rowKey={(r) => r.id}
          onRowClick={(r) => navigate(`/teacher/students/${r.id}`)}
        />
      )}
    </div>
  );
}
