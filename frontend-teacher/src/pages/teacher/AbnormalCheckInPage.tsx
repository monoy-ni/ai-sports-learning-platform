import { useEffect, useState } from "react";
import { Column, DataTable } from "../../shared/components/DataTable";
import { EmptyState } from "../../shared/components/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import { AbnormalCheckIn, CheckInReviewHistoryItem, getCheckInReviewHistory, listAbnormalCheckIns, reviewCheckIn } from "../../shared/api/teacherApi";

export function AbnormalCheckInPage() {
  const [rows, setRows] = useState<AbnormalCheckIn[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selected, setSelected] = useState<AbnormalCheckIn | null>(null);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      setRows(await listAbnormalCheckIns());
    } catch (e) {
      setError(e instanceof Error ? e.message : "加载失败");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => { load(); }, []);

  const columns: Column<AbnormalCheckIn>[] = [
    { key: "studentName", header: "学生", render: (r) => r.studentName },
    { key: "date", header: "日期", render: (r) => r.date },
    { key: "exerciseType", header: "运动", render: (r) => r.exerciseType },
    { key: "durationMinutes", header: "时长", render: (r) => `${r.durationMinutes}分钟` },
    { key: "abnormalReason", header: "异常原因", render: (r) => r.abnormalReason ?? "—" },
    { key: "status", header: "状态", render: () => <StatusBadge tone="warning">待核验</StatusBadge> },
  ];

  return (
    <div>
      <div className="filter-bar">
        <button type="button" className="ghost-button" onClick={load}>刷新</button>
      </div>
      {loading && <LoadingState label="加载异常打卡" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && (
        rows.length === 0 ? <EmptyState title="暂无异常打卡" /> :
        <DataTable columns={columns} rows={rows} rowKey={(r) => r.id} onRowClick={(r) => setSelected(r)} />
      )}
      {selected && <ReviewModal item={selected} onClose={() => setSelected(null)} onSaved={load} />}
    </div>
  );
}

function ReviewModal({ item, onClose, onSaved }: { item: AbnormalCheckIn; onClose: () => void; onSaved: () => void }) {
  const [abnormal, setAbnormal] = useState(true);
  const [abnormalReason, setAbnormalReason] = useState(item.abnormalReason ?? "");
  const [teacherReviewNote, setTeacherReviewNote] = useState(item.teacherReviewNote ?? "");
  const [editReason, setEditReason] = useState("");
  const [history, setHistory] = useState<CheckInReviewHistoryItem[]>([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getCheckInReviewHistory(item.id).then(setHistory).catch(() => {});
  }, [item.id]);

  async function submit() {
    setSaving(true);
    try {
      await reviewCheckIn(item.id, { abnormal, abnormalReason, teacherReviewNote, editReason });
      onSaved();
      onClose();
    } catch {
      // ignore
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="panel section-gap" role="dialog" aria-label="异常打卡审核">
      <h3 className="section-heading">审核: {item.studentName} - {item.date}</h3>
      <div className="form-grid">
        <label>标记异常
          <select value={abnormal ? "1" : "0"} onChange={(e) => setAbnormal(e.target.value === "1")}>
            <option value="1">是</option>
            <option value="0">否（已核验正常）</option>
          </select>
        </label>
        <label>异常原因<input value={abnormalReason} onChange={(e) => setAbnormalReason(e.target.value)} /></label>
        <label>教师审核备注<input value={teacherReviewNote} onChange={(e) => setTeacherReviewNote(e.target.value)} /></label>
        <label>修改原因<input value={editReason} onChange={(e) => setEditReason(e.target.value)} placeholder="说明本次修正理由" /></label>
      </div>
      <div className="button-row" style={{ marginTop: 8 }}>
        <button type="button" className="primary-button" onClick={submit} disabled={saving || !teacherReviewNote}>{saving ? "保存中…" : "提交审核"}</button>
        <button type="button" className="secondary-button" onClick={onClose}>取消</button>
      </div>

      <h3 className="section-heading" style={{ marginTop: 16 }}>修改记录</h3>
      {history.length === 0 ? <EmptyState title="暂无修改记录" /> : (
        <div className="timeline">
          {history.map((h) => (
            <div key={h.id} className="timeline-item">
              <div className="meta">审核人 #{h.reviewerId} · {new Date(h.createdAt).toLocaleString()}</div>
              <div>异常: {String(h.oldAbnormal)} → {String(h.newAbnormal)}</div>
              <div>备注: {h.oldTeacherReviewNote ?? "—"} → {h.newTeacherReviewNote}</div>
              {h.editReason && <div>理由: {h.editReason}</div>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
