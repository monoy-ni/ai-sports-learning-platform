package com.example.aisports.teacher.repository;

import com.example.aisports.teacher.domain.StudentTeacherNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentTeacherNoteRepository extends JpaRepository<StudentTeacherNote, Long> {
    Optional<StudentTeacherNote> findByStudentIdAndTermId(Long studentId, Long termId);
}
