import { Activity, CalendarCheck, ClipboardList, Home, Sparkles, UserRound } from "lucide-react";
import { NavLink, Outlet } from "react-router-dom";
import { AppShell } from "../../shared/components/AppShell";

const navItems = [
  { to: "/student/dashboard", label: "看板", icon: Home },
  { to: "/student/profile", label: "建档", icon: UserRound },
  { to: "/student/plan", label: "计划", icon: ClipboardList },
  { to: "/student/check-in", label: "打卡", icon: CalendarCheck },
  { to: "/student/check-ins", label: "记录", icon: Activity },
  { to: "/student/feedback", label: "AI 反馈", icon: Sparkles }
];

export function StudentLayout() {
  return (
    <AppShell title="学生运动工作台" subtitle="本周计划、今日打卡和 AI 反馈" navItems={navItems} role="STUDENT">
      <Outlet />
    </AppShell>
  );
}

