import { request } from "./httpClient";
import { DashboardMetric } from "./types";

export type TeacherDashboard = {
  metrics: DashboardMetric[];
  riskStudents: Array<{ id: number; name: string; reason: string; status: string }>;
};

export function getTeacherDashboard() {
  return request<TeacherDashboard>("/teacher/dashboard");
}

export function generateTermReport(studentId: number) {
  return request("/teacher/reports/generate", {
    method: "POST",
    body: JSON.stringify({ studentId })
  });
}

