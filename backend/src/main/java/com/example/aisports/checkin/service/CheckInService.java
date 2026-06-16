package com.example.aisports.checkin.service;

import com.example.aisports.airecord.client.AiServiceClient;
import com.example.aisports.airecord.dto.AiTaskResponse;
import com.example.aisports.airecord.service.AiRecordService;
import com.example.aisports.checkin.domain.CheckIn;
import com.example.aisports.checkin.domain.DataSource;
import com.example.aisports.checkin.dto.CheckInRequest;
import com.example.aisports.checkin.dto.CheckInResponse;
import com.example.aisports.checkin.dto.CheckInListItem;
import com.example.aisports.checkin.dto.CheckInReviewRequest;
import com.example.aisports.checkin.repository.CheckInRepository;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.common.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@Service
public class CheckInService {
    private final CheckInRepository repository;
    private final AiServiceClient aiServiceClient;
    private final AiRecordService aiRecordService;

    public CheckInService(CheckInRepository repository, AiServiceClient aiServiceClient, AiRecordService aiRecordService) {
        this.repository = repository;
        this.aiServiceClient = aiServiceClient;
        this.aiRecordService = aiRecordService;
    }

    @Transactional
    public CheckInResponse create(UserPrincipal principal, CheckInRequest request) {
        if (repository.existsByStudentIdAndCheckInDate(principal.id(), request.date())) {
            throw new BusinessException("CHECKIN_DUPLICATE", "当天已提交打卡");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setStudentId(principal.id());
        checkIn.setCheckInDate(request.date());
        checkIn.setWeather(request.weather());
        checkIn.setExerciseType(request.exerciseType());
        checkIn.setDurationMinutes(request.durationMinutes());
        checkIn.setDistanceKm(request.distanceKm());
        checkIn.setPaceMinutesPerKm(request.paceMinutesPerKm());
        checkIn.setFatigueLevel(request.fatigueLevel());
        checkIn.setFeeling(request.feeling());
        checkIn.setCompletedPlan(Boolean.TRUE.equals(request.completedPlan()));
        checkIn.setCampusRunScore(request.campusRunScore());
        checkIn.setDataSource(request.dataSource() == null ? DataSource.MANUAL : request.dataSource());
        markAbnormal(checkIn);
        CheckIn saved = repository.save(checkIn);

        Map<String, Object> payload = Map.of(
            "studentId", principal.id(),
            "checkInId", saved.getId(),
            "weather", saved.getWeather().name(),
            "fatigueLevel", saved.getFatigueLevel(),
            "inputVersion", "v0.1.0"
        );
        AiTaskResponse aiTask = aiServiceClient.generateDailyAnalysis(payload);
        aiRecordService.save(payload, aiTask);
        return CheckInResponse.from(saved, aiTask);
    }

    @Transactional(readOnly = true)
    public List<CheckInListItem> list(UserPrincipal principal) {
        return repository.findByStudentIdOrderByCheckInDateDesc(principal.id()).stream()
            .map(CheckInListItem::from)
            .toList();
    }

    @Transactional
    public CheckInListItem review(Long checkInId, CheckInReviewRequest request) {
        CheckIn checkIn = repository.findById(checkInId)
            .orElseThrow(() -> new BusinessException("CHECKIN_NOT_FOUND", "打卡记录不存在"));
        checkIn.setAbnormal(Boolean.TRUE.equals(request.abnormal()));
        checkIn.setAbnormalReason(request.abnormalReason());
        checkIn.setTeacherReviewNote(request.teacherReviewNote());
        return CheckInListItem.from(repository.save(checkIn));
    }

    private void markAbnormal(CheckIn checkIn) {
        if (checkIn.getCheckInDate().isAfter(LocalDate.now())) {
            checkIn.setAbnormal(true);
            checkIn.setAbnormalReason("未来日期");
            return;
        }
        if (checkIn.getPaceMinutesPerKm() != null && checkIn.getPaceMinutesPerKm().doubleValue() < 2.0) {
            checkIn.setAbnormal(true);
            checkIn.setAbnormalReason("配速明显不合理");
            return;
        }
        checkIn.setAbnormal(false);
        checkIn.setAbnormalReason(null);
    }
}
