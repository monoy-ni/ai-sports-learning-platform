import { StatusTone } from "../api/types";

const toneClass: Record<StatusTone, string> = {
  success: "success",
  warning: "warning",
  danger: "danger",
  info: "info",
  neutral: "neutral"
};

export function StatusBadge({ tone = "neutral", children }: { tone?: StatusTone; children: React.ReactNode }) {
  return <span className={`status-badge ${toneClass[tone]}`}>{children}</span>;
}

