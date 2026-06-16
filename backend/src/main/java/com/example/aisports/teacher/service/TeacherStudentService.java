package com.example.aisports.teacher.service;

import com.example.aisports.teacher.dto.StudentDetailResponse;
import com.example.aisports.teacher.dto.StudentListItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherStudentService {
    public List<StudentListItem> list() {
        return List.of(
            new StudentListItem(1L, "李同学", "一班", "正常", "今日已打卡", "未生成"),
            new StudentListItem(2L, "王同学", "一班", "偏高", "连续缺卡", "草稿")
        );
    }

    public StudentDetailResponse detail(Long studentId) {
        return new StudentDetailResponse(
            studentId,
            studentId == 2L ? "王同学" : "李同学",
            List.of("BMI 异常", "近 7 天缺卡 3 次", "配速异常记录待核验"),
            List.of("2026-06-16 跑步 30 分钟", "2026-06-15 室内无器械 24 分钟")
        );
    }
}

