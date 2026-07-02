import { useEffect, useState } from "react";
import { Column, DataTable } from "../../shared/components/DataTable";
import { EmptyState } from "../../shared/components/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState";
import { StatusBadge } from "../../shared/components/StatusBadge";
import {
  approveReport, generateReport, getReport, getReportEditHistory, listReports,
  TermReportDetail, TermReportEditHistoryItem, TermReportSummary, updateReport
} from "../../shared/api/teacherApi";
import { StatusTone } from "../../shared/api/types";

const STATUS_TONE: Record<string, StatusTone> = { DRAFT: "warning", APPROVED: "success" };
const STATUS_LABEL: Record<string, string> = { DRAFT: "草稿", APPROVED: "已审核" };

export function TermReportPage() {
  const [rows, setRows] = useState<TermReportSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  async function load() {
    setLoading(true);
    setError(null);
    try {
      setRows(await listReports());
    } catch (e) {
      setError(e instanceof Error ? e.message : "加载失败");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => { load(); }, []);

  const columns: Column<TermReportSummary>[] = [
    { key: "studentName", header: "学生", render: (r) => r.studentName },
    { key: "status", header: "状态", render: (r) => <StatusBadge tone={STATUS_TONE[r.status] ?? "neutral"}>{STATUS_LABEL[r.status] ?? r.status}</StatusBadge> },
    { key: "updatedAt", header: "更新时间", render: (r) => new Date(r.updatedAt).toLocaleString() },
  ];

  return (
    <div>
      <div className="filter-bar">
        <button type="button" className="ghost-button" onClick={load}>刷新</button>
      </div>
      {loading && <LoadingState label="加载报告" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && (
        rows.length === 0 ? <EmptyState title="暂无报告" description="为学生生成结课报告草稿" /> :
        <DataTable columns={columns} rows={rows} rowKey={(r) => r.reportId} onRowClick={(r) => setSelectedId(r.reportId)} />
      )}
      {selectedId && <ReportEditor reportId={selectedId} onClose={() => { setSelectedId(null); load(); }} />}
    </div>
  );
}

function ReportEditor({ reportId, onClose }: { reportId: number; onClose: () => void }) {
  const [report, setReport] = useState<TermReportDetail | null>(null);
  const [content, setContent] = useState("");
  const [note, setNote] = useState("");
  const [history, setHistory] = useState<TermReportEditHistoryItem[]>([]);
  const [showOriginal, setShowOriginal] = useState(false);
  const [busy, setBusy] = useState(false);
  const [studentId, setStudentId] = useState<number | null>(null);

  useEffect(() => {
    getReport(reportId).then((r) => { setReport(r); setContent(r.finalContent ?? ""); setNote(r.teacherReviewNote ?? ""); setStudentId(r.studentId); });
    getReportEditHistory(reportId).then(setHistory).catch(() => {});
  }, [reportId]);

  async function regenerate() {
    if (!studentId) return;
    setBusy(true);
    try {
      const r = await generateReport(studentId);
      setReport(r); setContent(r.finalContent ?? ""); setNote(r.teacherReviewNote ?? "");
      setHistory(await getReportEditHistory(reportId));
    } catch { /* ignore */ } finally { setBusy(false); }
  }
  async function save() {
    setBusy(true);
    try {
      const r = await updateReport(reportId, { finalContent: content, teacherReviewNote: note });
      setReport(r); setHistory(await getReportEditHistory(reportId));
    } catch { /* ignore */ } finally { setBusy(false); }
  }
  async function approve() {
    setBusy(true);
    try {
      const r = await approveReport(reportId);
      setReport(r); setHistory(await getReportEditHistory(reportId));
    } catch { /* ignore */ } finally { setBusy(false); }
  }

  if (!report) return <LoadingState label="加载报告详情" />;
  const isApproved = report.status === "APPROVED";

  return (
    <div className="panel section-gap" role="dialog" aria-label="结课报告编辑">
      <div className="section-heading" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <span>报告 #{report.reportId} <StatusBadge tone={STATUS_TONE[report.status] ?? "neutral"}>{STATUS_LABEL[report.status] ?? report.status}</StatusBadge></span>
        <button type="button" className="ghost-button" onClick={onClose}>关闭</button>
      </div>

      <div className="button-row" style={{ marginBottom: 8 }}>
        <button type="button" className="secondary-button" onClick={regenerate} disabled={busy}>重新生成草稿</button>
        <button type="button" className="secondary-button" onClick={() => setShowOriginal((v) => !v)}>{showOriginal ? "隐藏" : "查看"} AI 原文</button>
      </div>

      {showOriginal && (
        <div className="detail-section">
          <h3>AI 原文（不可编辑）</h3>
          <pre className="ai-original">{report.aiDraft ?? "（无 AI 原文）"}</pre>
        </div>
      )}

      <div className="detail-section">
        <h3>报告内容（可编辑）</h3>
        <textarea className="editor" aria-label="报告内容" value={content} onChange={(e) => setContent(e.target.value)} disabled={isApproved} />
      </div>

      <div className="detail-section">
        <h3>教师审核意见</h3>
        <textarea className="editor" aria-label="教师审核意见" value={note} onChange={(e) => setNote(e.target.value)} disabled={isApproved} style={{ minHeight: 80 }} />
      </div>

      <div className="button-row">
        <button type="button" className="primary-button" onClick={save} disabled={busy || isApproved}>保存修改</button>
        <button type="button" className="primary-button" onClick={approve} disabled={busy || isApproved}>审核通过</button>
      </div>

      <h3 className="section-heading" style={{ marginTop: 16 }}>编辑记录</h3>
      {history.length === 0 ? <EmptyState title="暂无编辑记录" /> : (
        <div className="timeline">
          {history.map((h) => (
            <div key={h.id} className="timeline-item">
              <div className="meta">编辑人 #{h.editorId} · {new Date(h.createdAt).toLocaleString()} · {h.action}</div>
              {h.action === "EDIT" && <div>{(h.oldFinalContent ?? "").slice(0, 40)}… → {(h.newFinalContent ?? "").slice(0, 40)}…</div>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
