package com.example.aisports.teacher.dto;

import com.example.aisports.term.domain.SchoolClass;

public record SchoolClassResponse(Long id, String className, String courseName, Long termId) {
    public static SchoolClassResponse from(SchoolClass c) {
        return new SchoolClassResponse(c.getId(), c.getClassName(), c.getCourseName(), c.getTermId());
    }
}
