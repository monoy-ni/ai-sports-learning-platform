package com.example.aisports.campusrun.service;

import com.example.aisports.campusrun.domain.CampusRunScoreRule;
import com.example.aisports.campusrun.dto.CampusRunScoreRuleRequest;
import com.example.aisports.campusrun.dto.CampusRunScoreRuleResponse;
import com.example.aisports.campusrun.repository.CampusRunScoreRuleRepository;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.term.domain.Term;
import com.example.aisports.term.repository.TermRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CampusRunScoreRuleService {
    private final CampusRunScoreRuleRepository repository;
    private final TermRepository termRepository;
    private final ObjectMapper objectMapper;

    public CampusRunScoreRuleService(CampusRunScoreRuleRepository repository,
                                     TermRepository termRepository,
                                     ObjectMapper objectMapper) {
        this.repository = repository;
        this.termRepository = termRepository;
        this.objectMapper = objectMapper;
    }

    public CampusRunScoreRuleResponse getActive(Long termId) {
        Term term = resolveTerm(termId);
        CampusRunScoreRule rule = repository.findByTermIdAndIsActiveTrue(term.getId()).orElse(null);
        return toResponse(rule);
    }

    @Transactional
    public CampusRunScoreRuleResponse update(CampusRunScoreRuleRequest request) {
        Term term = resolveTerm(request.termId());
        // 校验 JSON 合法
        try {
            objectMapper.readTree(request.rulesJson());
        } catch (Exception ex) {
            throw new BusinessException("RULE_JSON_INVALID", "评分规则 JSON 不合法");
        }
        // 旧规则置灰, 再插新行 (保留历史)
        Optional<CampusRunScoreRule> existing = repository.findByTermIdAndIsActiveTrue(term.getId());
        existing.ifPresent(r -> {
            r.setIsActive(false);
            repository.save(r);
        });
        CampusRunScoreRule rule = new CampusRunScoreRule();
        rule.setTermId(term.getId());
        rule.setRuleName(request.ruleName());
        rule.setRulesJson(request.rulesJson());
        rule.setIsActive(true);
        repository.save(rule);
        return toResponse(rule);
    }

    private Term resolveTerm(Long termId) {
        if (termId != null) {
            return termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException("TERM_NOT_FOUND", "学期不存在"));
        }
        return termRepository.findByIsCurrentTrue()
            .orElseThrow(() -> new BusinessException("TERM_NOT_FOUND", "未配置当前学期"));
    }

    private CampusRunScoreRuleResponse toResponse(CampusRunScoreRule rule) {
        if (rule == null) return null;
        return new CampusRunScoreRuleResponse(rule.getId(), rule.getTermId(), rule.getRuleName(),
            rule.getRulesJson(), Boolean.TRUE.equals(rule.getIsActive()));
    }
}
