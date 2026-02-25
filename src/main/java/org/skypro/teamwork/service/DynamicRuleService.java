package org.skypro.teamwork.service;

import org.skypro.teamwork.dto.DynamicRuleDto;
import org.skypro.teamwork.models.DynamicRule;
import org.skypro.teamwork.repository.jpa.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleEvaluator ruleEvaluator;

    public DynamicRuleService(DynamicRuleRepository dynamicRuleRepository,
                              RuleEvaluator ruleEvaluator) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleEvaluator = ruleEvaluator;
    }

    @Transactional
    public DynamicRuleDto createRule(DynamicRuleDto ruleDto) {
        if (dynamicRuleRepository.existsByProductId(ruleDto.getProductId())) {
            throw new IllegalArgumentException("Rule with product ID already exists");
        }

        DynamicRule rule = new DynamicRule(
                ruleDto.getProductName(),
                ruleDto.getProductId(),
                ruleDto.getProductText(),
                ruleDto.getRule()
        );

        DynamicRule savedRule = dynamicRuleRepository.save(rule);
        return convertToDto(savedRule);
    }

    @Transactional(readOnly = true)
    public List<DynamicRuleDto> getAllRules() {
        return dynamicRuleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRule(UUID productId) {
        DynamicRule rule = dynamicRuleRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found with product ID: " + productId));
        dynamicRuleRepository.delete(rule);
    }

    public List<DynamicRule> getApplicableRulesForUser(UUID userId) {
        return dynamicRuleRepository.findAll().stream()
                .filter(rule -> ruleEvaluator.evaluateRule(userId, rule.getRule()))
                .collect(Collectors.toList());
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