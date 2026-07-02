package com.example.aisports.teacher.repository;

import com.example.aisports.auth.domain.Role;
import com.example.aisports.auth.domain.UserAccount;
import com.example.aisports.checkin.domain.CheckIn;
import com.example.aisports.checkin.domain.DataSource;
import com.example.aisports.checkin.domain.ExerciseType;
import com.example.aisports.checkin.domain.WeatherStatus;
import com.example.aisports.checkin.repository.CheckInRepository;
import com.example.aisports.term.domain.Enrollment;
import com.example.aisports.term.domain.SchoolClass;
import com.example.aisports.term.domain.Term;
import com.example.aisports.term.repository.EnrollmentRepository;
import com.example.aisports.term.repository.SchoolClassRepository;
import com.example.aisports.term.repository.TermRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TeacherRepositoryTest {

    @Autowired TermRepository termRepository;
    @Autowired SchoolClassRepository classRepository;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired CheckInRepository checkInRepository;

    @Test
    void resolvesStudentIdsByTeacherAndTerm() {
        Term term = new Term();
        term.setTermCode("2026-Spring");
        term.setName("2026春季");
        term.setStartDate(LocalDate.now().minusDays(30));
        term.setEndDate(LocalDate.now().plusDays(30));
        term.setIsCurrent(true);
        term = termRepository.save(term);

        SchoolClass klass = new SchoolClass();
        klass.setClassName("计算机2401");
        klass.setCourseName("大学体育");
        klass.setTermId(term.getId());
        klass.setTeacherId(2L);
        klass = classRepository.save(klass);

        Enrollment e = new Enrollment();
        e.setStudentId(10L);
        e.setClassId(klass.getId());
        e.setTermId(term.getId());
        enrollmentRepository.save(e);

        List<Long> ids = enrollmentRepository.findStudentIdsByTeacherAndTerm(2L, term.getId());
        assertThat(ids).contains(10L);

        List<Long> byClass = enrollmentRepository.findStudentIdsByClassId(klass.getId());
        assertThat(byClass).contains(10L);
    }

    @Test
    void findsCheckInsInRangeAndAbnormal() {
        Term term = new Term();
        term.setTermCode("2026-Spring");
        term.setName("2026春季");
        term.setStartDate(LocalDate.now().minusDays(10));
        term.setEndDate(LocalDate.now().plusDays(10));
        term.setIsCurrent(true);
        termRepository.save(term);

        CheckIn c = new CheckIn();
        c.setStudentId(10L);
        c.setCheckInDate(LocalDate.now().minusDays(2));
        c.setWeather(WeatherStatus.SUNNY);
        c.setExerciseType(ExerciseType.RUNNING);
        c.setDurationMinutes(30);
        c.setDistanceKm(BigDecimal.valueOf(5));
        c.setPaceMinutesPerKm(BigDecimal.valueOf(6));
        c.setFatigueLevel(4);
        c.setCompletedPlan(true);
        c.setDataSource(DataSource.MANUAL);
        c.setAbnormal(false);
        checkInRepository.save(c);

        CheckIn ab = new CheckIn();
        ab.setStudentId(10L);
        ab.setCheckInDate(LocalDate.now().minusDays(1));
        ab.setWeather(WeatherStatus.SUNNY);
        ab.setExerciseType(ExerciseType.RUNNING);
        ab.setDurationMinutes(20);
        ab.setFatigueLevel(4);
        ab.setCompletedPlan(true);
        ab.setDataSource(DataSource.MANUAL);
        ab.setAbnormal(true);
        ab.setAbnormalReason("配速异常");
        checkInRepository.save(ab);

        List<CheckIn> inRange = checkInRepository.findByStudentIdInAndCheckInDateBetweenOrderByStudentIdAscCheckInDateAsc(
            List.of(10L), LocalDate.now().minusDays(10), LocalDate.now());
        assertThat(inRange).hasSize(2);

        List<CheckIn> abnormals = checkInRepository.findByStudentIdInAndAbnormalTrueOrderByCheckInDateDesc(List.of(10L));
        assertThat(abnormals).hasSize(1);
        assertThat(abnormals.get(0).getAbnormalReason()).isEqualTo("配速异常");
    }
}
