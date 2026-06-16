# 领域模型

核心领域模块：

- Auth/User：账号、角色和身份绑定。
- Health Profile：身高、体重、BMI、肺活量、疾病状态、运动目标、班级和学期归属。
- Sport Plan：每名学生每学期只允许一个成功生成的计划，除非被授权重置。
- Check-in：每名学生每日一条打卡记录，包含异常数据标记。
- Campus Run：校园跑成绩和来源标记，例如 `MANUAL`、`API_SYNC`。
- AI Record：任务元信息、结构化输出、图片地址、失败原因和重试状态。
- Term Report：AI 草稿、教师编辑、审核状态和最终报告。
