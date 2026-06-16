import { createBrowserRouter, Navigate } from "react-router-dom";
import { App } from "../App";
import { AuthLayout } from "./layout/AuthLayout";
import { StudentLayout } from "./layout/StudentLayout";
import { TeacherLayout } from "./layout/TeacherLayout";
import { LoginPage } from "../pages/auth/LoginPage";
import { AiFeedbackPage } from "../pages/student/AiFeedbackPage";
import { CheckInHistoryPage } from "../pages/student/CheckInHistoryPage";
import { CheckInPage } from "../pages/student/CheckInPage";
import { DashboardPage } from "../pages/student/DashboardPage";
import { HealthProfilePage } from "../pages/student/HealthProfilePage";
import { SportPlanPage } from "../pages/student/SportPlanPage";
import { AbnormalCheckInPage } from "../pages/teacher/AbnormalCheckInPage";
import { StudentDetailPage } from "../pages/teacher/StudentDetailPage";
import { StudentListPage } from "../pages/teacher/StudentListPage";
import { TeacherDashboardPage } from "../pages/teacher/TeacherDashboardPage";
import { TermReportPage } from "../pages/teacher/TermReportPage";

export const router = createBrowserRouter([
  { path: "/", element: <App /> },
  {
    path: "/login",
    element: <AuthLayout />,
    children: [{ index: true, element: <LoginPage /> }]
  },
  {
    path: "/student",
    element: <StudentLayout />,
    children: [
      { index: true, element: <Navigate to="/student/dashboard" replace /> },
      { path: "dashboard", element: <DashboardPage /> },
      { path: "profile", element: <HealthProfilePage /> },
      { path: "plan", element: <SportPlanPage /> },
      { path: "check-in", element: <CheckInPage /> },
      { path: "check-ins", element: <CheckInHistoryPage /> },
      { path: "feedback", element: <AiFeedbackPage /> }
    ]
  },
  {
    path: "/teacher",
    element: <TeacherLayout />,
    children: [
      { index: true, element: <Navigate to="/teacher/dashboard" replace /> },
      { path: "dashboard", element: <TeacherDashboardPage /> },
      { path: "students", element: <StudentListPage /> },
      { path: "students/:studentId", element: <StudentDetailPage /> },
      { path: "abnormal-check-ins", element: <AbnormalCheckInPage /> },
      { path: "reports", element: <TermReportPage /> }
    ]
  }
]);

