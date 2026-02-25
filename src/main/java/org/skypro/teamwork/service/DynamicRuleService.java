package org.skypro.teamwork.service;

import org.skypro.teamwork.dto.DynamicRuleDto;
import org.skypro.teamwork.dto.RuleStatDto;
import org.skypro.teamwork.models.DynamicRule;
import org.skypro.teamwork.models.RuleStat;
import org.skypro.teamwork.repository.jpa.DynamicRuleRepository;
import org.skypro.teamwork.repository.jpa.RuleStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository ruleRepository;
    private final RuleStatRepository statRepository;
    private final RuleEvaluator ruleEvaluator;

    public DynamicRuleService(DynamicRuleRepository ruleRepository,
                              RuleStatRepository statRepository,
                              RuleEvaluator ruleEvaluator) {
        this.ruleRepository = ruleRepository;
        this.statRepository = statRepository;
        this.ruleEvaluator = ruleEvaluator;
    }

    @Transactional
    public DynamicRuleDto createRule(DynamicRuleDto ruleDto) {
        if (ruleRepository.existsByProductId(ruleDto.getProductId())) {
            throw new IllegalArgumentException("Rule with product ID already exists");
        }

        DynamicRule rule = new DynamicRule(
                ruleDto.getProductName(),
                ruleDto.getProductId(),
                ruleDto.getProductText(),
                ruleDto.getRule()
        );

        DynamicRule savedRule = ruleRepository.save(rule);

        RuleStat stat = new RuleStat(savedRule.getId());
        statRepository.save(stat);

        return convertToDto(savedRule);
    }

    @Transactional(readOnly = true)
    public List<DynamicRuleDto> getAllRules() {  // ДЛЯ КОНТРОЛЛЕРА - возвращает DTO
        return ruleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DynamicRule> getAllDynamicRules() {  // ДЛЯ СЕРВИСА - возвращает сущности
        return ruleRepository.findAll();
    }

    @Transactional
    public void deleteRule(UUID productId) {
        DynamicRule rule = ruleRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found with product ID: " + productId));

        statRepository.deleteByRuleId(rule.getId());

        ruleRepository.delete(rule);
    }

    public List<DynamicRule> getApplicableRulesForUser(UUID userId) {
        List<DynamicRule> allRules = ruleRepository.findAll();
        List<DynamicRule> applicableRules = new ArrayList<>();

        for (DynamicRule rule : allRules) {
            if (ruleEvaluator.evaluateRule(userId, rule.getRule())) {
                applicableRules.add(rule);

                statRepository.findByRuleId(rule.getId())
                        .ifPresent(stat -> {
                            stat.increment();
                            statRepository.save(stat);
                        });
            }
        }

        return applicableRules;
    }

    public List<RuleStatDto> getRuleStats() {
        List<DynamicRule> allRules = ruleRepository.findAll();
        List<RuleStatDto> stats = new ArrayList<>();

        for (DynamicRule rule : allRules) {
            RuleStat stat = statRepository.findByRuleId(rule.getId())
                    .orElse(new RuleStat(rule.getId()));

            stats.add(new RuleStatDto(rule.getId(), stat.getCount()));
        }

        // Сортируем по убыванию count
        stats.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        return stats;
    }

    private DynamicRuleDto convertToDto(DynamicRule rule) {
        return new DynamicRuleDto(
                rule.getId(),
                rule.getProductName(),
                rule.getProductId(),
                rule.getProductText(),
                rule.getRule()
        );
    }
}