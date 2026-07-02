package com.example.aisports.term.repository;

import com.example.aisports.term.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("select e.studentId from Enrollment e where e.classId = :classId")
    List<Long> findStudentIdsByClassId(@Param("classId") Long classId);

    @Query("select e.studentId from Enrollment e where e.classId in " +
           "(select c.id from SchoolClass c where c.teacherId = :teacherId and c.termId = :termId)")
    List<Long> findStudentIdsByTeacherAndTerm(@Param("teacherId") Long teacherId, @Param("termId") Long termId);

    List<Enrollment> findByStudentIdAndTermId(Long studentId, Long termId);

    List<Enrollment> findByStudentIdInAndTermId(Collection<Long> studentIds, Long termId);
}
