package com.example.aisports.student.service;

import com.example.aisports.student.dto.DashboardMetric;
import com.example.aisports.student.dto.StudentDashboardResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentDashboardService {
    public StudentDashboardResponse currentDashboard() {
        return new StudentDashboardResponse(
            List.of(
                new DashboardMetric("本周打卡", "3/5", "计划完成率 60%", "info"),
                new DashboardMetric("校园跑分", "82", "较上周 +4", "success"),
                new DashboardMetric("BMI", "21.8", "正常范围", "success"),
                new DashboardMetric("AI 任务", "正常", "今日建议已生成", "success")
            ),
            List.of(
                "普通跑 2 次，每次 25-30 分钟，配速 6'30\"-7'10\"/km。",
                "节奏跑 1 次，含 8 分钟热身和 6 分钟拉伸。",
                "雨天切换为低冲击有氧、核心训练和动态拉伸。"
            ),
            "今天适合轻中强度跑步。若疲劳程度超过 7 分，建议降低配速并增加恢复时间。"
        );
    }
}

