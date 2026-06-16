# 登录与 RBAC 设计

角色：

- `STUDENT`
- `TEACHER`
- `ADMIN`

`0.1.0` 使用 JWT。Token 中包含用户名和角色声明。前端路由守卫只改善体验，真实权限由 Spring Security 强制执行。

本地开发内置学生和教师演示账号。
