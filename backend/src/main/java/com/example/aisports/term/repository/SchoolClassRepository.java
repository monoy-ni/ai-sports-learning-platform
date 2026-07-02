package com.example.aisports.term.repository;

import com.example.aisports.term.domain.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findByTeacherId(Long teacherId);
    List<SchoolClass> findByTermId(Long termId);
    Optional<SchoolClass> findByIdAndTeacherId(Long id, Long teacherId);
}
