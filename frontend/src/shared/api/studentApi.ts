import { request } from "./httpClient";
import { DashboardMetric } from "./types";

export type StudentDashboard = {
  metrics: DashboardMetric[];
  weeklyPlan: string[];
  todayAdvice: string;
};

export type HealthProfilePayload = {
  gender: string;
  age: number;
  heightCm: number;
  weightKg: number;
  vitalCapacity: number;
  diseaseStatus: "NONE" | "HAS_DISEASE" | "UNKNOWN";
  diseaseNote?: string;
  sportGoal: string;
  weeklyFrequency: number;
  bodyType: string;
};

export function getStudentDashboard() {
  return request<StudentDashboard>("/student/dashboard");
}

export function saveHealthProfile(payload: HealthProfilePayload) {
  return request("/student/health-profile", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function generateSportPlan() {
  return request("/student/sport-plans/generate", { method: "POST" });
}

export function createCheckIn(payload: Record<string, unknown>) {
  return request("/student/check-ins", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

