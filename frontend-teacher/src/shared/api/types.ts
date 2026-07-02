export type ApiResponse<T> = {
  code: string;
  message: string;
  data: T;
  traceId?: string;
};

export type Role = "STUDENT" | "TEACHER" | "ADMIN";

export type AiTaskStatus = "SUCCESS" | "FAILED" | "PENDING_RETRY" | "RUNNING";

export type StatusTone = "success" | "warning" | "danger" | "info" | "neutral";

export type DashboardMetric = {
  label: string;
  value: string;
  helper: string;
  tone?: StatusTone;
};

