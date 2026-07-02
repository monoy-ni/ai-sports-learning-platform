package com.example.aisports.term.repository;

import com.example.aisports.term.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByTermCode(String termCode);
    Optional<Term> findByIsCurrentTrue();
}
