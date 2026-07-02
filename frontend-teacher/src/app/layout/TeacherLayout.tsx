import { AlertTriangle, BarChart3, FileText, GraduationCap, Users } from "lucide-react";
import { Outlet } from "react-router-dom";
import { AppShell } from "../../shared/components/AppShell";

const navItems = [
  { to: "/teacher/dashboard", label: "总览", icon: BarChart3 },
  { to: "/teacher/students", label: "学生", icon: Users },
  { to: "/teacher/abnormal-check-ins", label: "异常", icon: AlertTriangle },
  { to: "/teacher/reports", label: "报告", icon: FileText }
];

export function TeacherLayout() {
  return (
    <AppShell title="教师管理工作台" subtitle="班级运动状态、风险关注和结课报告" navItems={navItems} role="TEACHER" brandIcon={GraduationCap}>
      <Outlet />
    </AppShell>
  );
}

