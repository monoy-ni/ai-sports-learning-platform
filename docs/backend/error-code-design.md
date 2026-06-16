# 错误码设计

使用统一响应结构：

```json
{
  "code": "CHECKIN_DUPLICATE",
  "message": "Check-in already exists for the date.",
  "data": null,
  "traceId": "..."
}
```

错误码按模块加前缀：

- `AUTH_`
- `PROFILE_`
- `PLAN_`
- `CHECKIN_`
- `AI_`
- `REPORT_`
