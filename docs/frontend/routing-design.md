# 路由设计

## 公共路由

- `/login`：登录页。

## 学生端路由

- `/student/dashboard`
- `/student/profile`
- `/student/plan`
- `/student/check-in`
- `/student/check-ins`
- `/student/feedback`

## 教师端路由

- `/teacher/dashboard`
- `/teacher/students`
- `/teacher/students/:studentId`
- `/teacher/abnormal-check-ins`
- `/teacher/reports`

前端路由守卫只负责体验层拦截，真实权限由 Spring Security 在后端强制执行。
