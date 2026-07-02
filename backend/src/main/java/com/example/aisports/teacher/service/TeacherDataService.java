package com.example.aisports.teacher.service;

import com.example.aisports.auth.domain.UserAccount;
import com.example.aisports.auth.repository.UserAccountRepository;
import com.example.aisports.checkin.domain.CheckIn;
import com.example.aisports.checkin.repository.CheckInRepository;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.healthprofile.domain.HealthProfile;
import com.example.aisports.healthprofile.repository.HealthProfileRepository;
import com.example.aisports.term.domain.SchoolClass;
import com.example.aisports.term.domain.Term;
import com.example.aisports.term.repository.EnrollmentRepository;
import com.example.aisports.term.repository.SchoolClassRepository;
import com.example.aisports.term.repository.TermRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 教师端共享数据解析: 学期/班级/学生集合 + 批量加载, 供看板/列表/详情/报告复用.
 */
@Service
public class TeacherDataService {
    private final TermRepository termRepository;
    private final SchoolClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserAccountRepository userRepository;
    private final HealthProfileRepository profileRepository;
    private final CheckInRepository checkInRepository;

    public TeacherDataService(TermRepository termRepository,
                              SchoolClassRepository classRepository,
                              EnrollmentRepository enrollmentRepository,
                              UserAccountRepository userRepository,
                              HealthProfileRepository profileRepository,
                              CheckInRepository checkInRepository) {
        this.termRepository = termRepository;
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.checkInRepository = checkInRepository;
    }

    public Term resolveTerm(Long termId) {
        if (termId != null) {
            return termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException("TERM_NOT_FOUND", "学期不存在"));
        }
        return termRepository.findByIsCurrentTrue()
            .orElseThrow(() -> new BusinessException("TERM_NOT_FOUND", "未配置当前学期"));
    }

    /** 解析某教师某学期(或某班级)下的学生 ID, 校验班级归属. */
    public List<Long> resolveStudentIds(Long teacherId, Long classId, Long termId) {
        if (classId != null) {
            SchoolClass schoolClass = classRepository.findByIdAndTeacherId(classId, teacherId)
                .orElseThrow(() -> new BusinessException("FORBIDDEN_CLASS", "无权访问该班级"));
            return enrollmentRepository.findStudentIdsByClassId(classId);
        }
        return enrollmentRepository.findStudentIdsByTeacherAndTerm(teacherId, termId);
    }

    public Map<Long, UserAccount> loadUsers(Collection<Long> studentIds) {
        return userRepository.findByIdIn(studentIds).stream()
            .collect(Collectors.toMap(UserAccount::getId, Function.identity()));
    }

    public Map<Long, HealthProfile> loadProfiles(Collection<Long> studentIds) {
        return profileRepository.findByStudentIdIn(studentIds).stream()
            .collect(Collectors.toMap(HealthProfile::getStudentId, Function.identity()));
    }

    public List<CheckIn> loadCheckIns(Collection<Long> studentIds, LocalDate start, LocalDate end) {
        if (studentIds.isEmpty()) {
            return List.of();
        }
        return checkInRepository.findByStudentIdInAndCheckInDateBetweenOrderByStudentIdAscCheckInDateAsc(
            studentIds, start, end);
    }

    public List<SchoolClass> classesOfTeacher(Long teacherId) {
        return classRepository.findByTeacherId(teacherId);
    }

    public List<Term> allTerms() {
        return termRepository.findAll();
    }

    /** 学生 -> 班级名 (取该学期主班级) */
    public Map<Long, String> loadClassNamesByStudent(Collection<Long> studentIds, Long termId) {
        if (studentIds.isEmpty()) {
            return Map.of();
        }
        List<com.example.aisports.term.domain.Enrollment> enrollments =
            enrollmentRepository.findByStudentIdInAndTermId(studentIds, termId);
        List<Long> classIds = enrollments.stream()
            .map(com.example.aisports.term.domain.Enrollment::getClassId)
            .distinct()
            .toList();
        Map<Long, String> classNames = classRepository.findAllById(classIds).stream()
            .collect(Collectors.toMap(SchoolClass::getId, SchoolClass::getClassName));
        return enrollments.stream()
            .collect(Collectors.toMap(
                com.example.aisports.term.domain.Enrollment::getStudentId,
                e -> classNames.getOrDefault(e.getClassId(), ""),
                (a, b) -> a));
    }
}
