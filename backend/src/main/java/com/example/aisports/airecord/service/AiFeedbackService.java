package com.example.aisports.airecord.service;

import com.example.aisports.airecord.dto.AiFeedbackResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiFeedbackService {
    public AiFeedbackResponse latest() {
        return new AiFeedbackResponse(
            List.of(
                "本次训练完成度较好，配速稳定。",
                "建议运动后进行小腿后侧、髂腰肌和臀部拉伸。",
                "如出现胸闷、头晕或持续疼痛，应停止运动并咨询教师或医生。"
            ),
            "MOCK_SUCCESS",
            null
        );
    }
}

