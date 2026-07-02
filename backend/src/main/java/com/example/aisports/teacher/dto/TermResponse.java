package com.example.aisports.teacher.dto;

import com.example.aisports.term.domain.Term;

import java.time.LocalDate;

public record TermResponse(Long id, String termCode, String name, LocalDate startDate, LocalDate endDate, boolean current) {
    public static TermResponse from(Term t) {
        return new TermResponse(t.getId(), t.getTermCode(), t.getName(), t.getStartDate(), t.getEndDate(),
            Boolean.TRUE.equals(t.getIsCurrent()));
    }
}
