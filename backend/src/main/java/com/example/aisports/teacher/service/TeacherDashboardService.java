package com.example.aisports.teacher.service;

import com.example.aisports.student.dto.DashboardMetric;
import com.example.aisports.teacher.dto.TeacherDashboardResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherDashboardService {
    public TeacherDashboardResponse dashboard() {
        return new TeacherDashboardResponse(
            List.of(
                new DashboardMetric("学生总数", "128", "3 个班级", "info"),
                new DashboardMetric("已建档", "116", "完成率 90.6%", "success"),
                new DashboardMetric("异常数据", "7", "需人工关注", "warning"),
                new DashboardMetric("平均分", "78.4", "校园跑", "neutral")
            ),
            List.of(
                new TeacherDashboardResponse.RiskStudent(1L, "王同学", "连续缺卡", "待关注"),
                new TeacherDashboardResponse.RiskStudent(2L, "赵同学", "配速异常", "待核验")
            )
        );
    }
}

