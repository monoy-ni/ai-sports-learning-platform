import { request } from "./httpClient";
import { DashboardMetric, StatusTone } from "./types";

// ---- 基础类型 ----

export type Term = { id: number; termCode: string; name: string; startDate: string; endDate: string; current: boolean };
export type SchoolClass = { id: number; className: string; courseName: string; termId: number };

export type RiskStudent = { id: number; name: string; reason: string; status: string };

export type TeacherDashboard = {
  metrics: DashboardMetric[];
  exercise: {
    totalCheckIns: number;
    avgDurationMinutes: number;
    avgDistanceKm: number;
    avgPaceMinutesPerKm: number;
    planCompletionRate: number;
  };
  bmiDistribution: { underweight: number; normal: number; overweight: number; obese: number };
  vitalCapacity: { excellent: number; pass: number; fail: number };
  consecutiveMissStudents: RiskStudent[];
  lowVolumeStudents: RiskStudent[];
  abnormalDataStudents: RiskStudent[];
  healthRiskStudents: RiskStudent[];
};

export type StudentListItem = {
  id: number;
  name: string;
  studentNumber: string;
  className: string;
  bmi: number | null;
  bmiCategory: string;
  campusRunScore: number | null;
  checkInStatus: string;
  reportStatus: string;
  riskFlagCount: number;
};

export type StudentListQuery = {
  classId?: number;
  termId?: number;
  q?: string;
  bmiCategory?: string;
  scoreMin?: number;
  scoreMax?: number;
  healthRisk?: boolean;
  checkInStatus?: string;
  reportStatus?: string;
  sort?: string;
  dir?: string;
};

export type RiskFlag = { type: string; label: string; severity: string };
export type CheckInListItem = {
  id: number; date: string; exerciseType: string; durationMinutes: number; abnormal: boolean; abnormalReason: string | null;
};
export type HealthTrendPoint = { date: string; durationMinutes: number | null; distanceKm: number | null };

export type StudentDetail = {
  id: number;
  name: string;
  studentNumber: string;
  className: string;
  healthProfile: {
    gender: string; age: number; heightCm: number; weightKg: number; bmi: number; vitalCapacity: number;
    diseaseStatus: string; diseaseNote: string | null; sportGoal: string; weeklyFrequency: number; bodyType: string;
  } | null;
  sportPlan: { planId: number; termId: string; generationStatus: string; weeklyPlan: string } | null;
  recentCheckIns: CheckInListItem[];
  campusRunScore: { score: number; dataSource: string };
  healthTrend: { points: HealthTrendPoint[] };
  aiDailySuggestion: { summary: string; suggestions: string[]; imageStatus: string; imageUrl: string };
  riskFlags: RiskFlag[];
  teacherNote: string;
};

export type AbnormalCheckIn = {
  id: number; studentId: number; studentName: string; date: string; exerciseType: string;
  durationMinutes: number; abnormalReason: string | null; teacherReviewNote: string | null;
};

export type CheckInReviewHistoryItem = {
  id: number; reviewerId: number; oldAbnormal: boolean | null; oldAbnormalReason: string | null;
  oldTeacherReviewNote: string | null; newAbnormal: boolean; newAbnormalReason: string | null;
  newTeacherReviewNote: string; editReason: string | null; createdAt: string;
};

export type CampusRunScoreRule = { id: number; termId: number; ruleName: string; rulesJson: string; active: boolean };

export type TermReportSummary = { reportId: number; studentId: number; studentName: string; status: string; updatedAt: string };
export type TermReportDetail = {
  reportId: number; studentId: number; status: string; aiDraft: string | null;
  finalContent: string; teacherReviewNote: string | null;
};
export type TermReportEditHistoryItem = {
  id: number; editorId: number; oldFinalContent: string | null; newFinalContent: string | null;
  oldTeacherReviewNote: string | null; newTeacherReviewNote: string | null; action: string; createdAt: string;
};

// ---- 调用 ----

function qs(params: Record<string, string | number | boolean | undefined>): string {
  const usp = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== "") usp.set(k, String(v));
  });
  const s = usp.toString();
  return s ? `?${s}` : "";
}

export const getTerms = () => request<Term[]>("/teacher/terms");
export const getClasses = (termId?: number) => request<SchoolClass[]>(`/teacher/classes${qs({ termId })}`);
export const getTeacherDashboard = (classId?: number, termId?: number) =>
  request<TeacherDashboard>(`/teacher/dashboard${qs({ classId, termId })}`);

export const listStudents = (query: StudentListQuery) =>
  request<StudentListItem[]>(`/teacher/students${qs(query)}`);
export const getStudentDetail = (studentId: number, termId?: number) =>
  request<StudentDetail>(`/teacher/students/${studentId}${qs({ termId })}`);
export const getStudentNote = (studentId: number, termId?: number) =>
  request<{ studentId: number; termId: number; content: string }>(`/teacher/students/${studentId}/note${qs({ termId })}`);
export const updateStudentNote = (studentId: number, termId: number, content: string) =>
  request(`/teacher/students/${studentId}/note`, { method: "PUT", body: JSON.stringify({ termId, content }) });

export const listAbnormalCheckIns = (classId?: number, termId?: number) =>
  request<AbnormalCheckIn[]>(`/teacher/check-ins/abnormal${qs({ classId, termId })}`);
export const reviewCheckIn = (checkInId: number, body: { abnormal: boolean; abnormalReason: string; teacherReviewNote: string; editReason?: string }) =>
  request(`/teacher/check-ins/${checkInId}/review`, { method: "PATCH", body: JSON.stringify(body) });
export const getCheckInReviewHistory = (checkInId: number) =>
  request<CheckInReviewHistoryItem[]>(`/teacher/check-ins/${checkInId}/review-history`);

export const getCampusRunRules = (termId?: number) =>
  request<CampusRunScoreRule | null>(`/teacher/campus-run-rules${qs({ termId })}`);
export const updateCampusRunRules = (body: { termId: number; ruleName: string; rulesJson: string }) =>
  request<CampusRunScoreRule>(`/teacher/campus-run-rules`, { method: "PUT", body: JSON.stringify(body) });

export const generateReport = (studentId: number, termId?: number, teacherNote?: string) =>
  request<TermReportDetail>(`/teacher/reports/generate`, { method: "POST", body: JSON.stringify({ studentId, termId, teacherNote }) });
export const generateBatchReports = (studentIds: number[], termId?: number, teacherNote?: string) =>
  request<TermReportDetail[]>(`/teacher/reports/generate-batch`, { method: "POST", body: JSON.stringify({ studentIds, termId, teacherNote }) });
export const listReports = (termId?: number, classId?: number, status?: string) =>
  request<TermReportSummary[]>(`/teacher/reports${qs({ termId, classId, status })}`);
export const getReport = (reportId: number) => request<TermReportDetail>(`/teacher/reports/${reportId}`);
export const updateReport = (reportId: number, body: { finalContent: string; teacherReviewNote?: string }) =>
  request<TermReportDetail>(`/teacher/reports/${reportId}`, { method: "PUT", body: JSON.stringify(body) });
export const approveReport = (reportId: number) =>
  request<TermReportDetail>(`/teacher/reports/${reportId}/approve`, { method: "POST" });
export const getReportEditHistory = (reportId: number) =>
  request<TermReportEditHistoryItem[]>(`/teacher/reports/${reportId}/edit-history`);

export type { DashboardMetric };
