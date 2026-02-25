package org.skypro.teamwork.service;

import org.skypro.teamwork.models.DynamicRule;
import org.skypro.teamwork.models.Recommendation;
import org.skypro.teamwork.models.RecommendationsResponse;
import org.skypro.teamwork.repository.RecommendationRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final DynamicRuleService dynamicRuleService;
    private final RuleEvaluator ruleEvaluator;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 DynamicRuleService dynamicRuleService,
                                 RuleEvaluator ruleEvaluator) {
        this.recommendationRepository = recommendationRepository;
        this.dynamicRuleService = dynamicRuleService;
        this.ruleEvaluator = ruleEvaluator;
    }

    @Cacheable(value = "recommendations", key = "#userId")
    public RecommendationsResponse getRecommendationsForUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Неправильный ID");
        }

        List<Recommendation> recommendations = new ArrayList<>();

        List<Recommendation> staticRecommendations =
                recommendationRepository.findRecommendationsForUser(userId);
        recommendations.addAll(staticRecommendations);

        List<DynamicRule> dynamicRules = dynamicRuleService.getAllDynamicRules();
        for (DynamicRule rule : dynamicRules) {
            if (ruleEvaluator.evaluateRule(userId, rule.getRule())) {
                recommendations.add(new Recommendation(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                ));
            }
        }

        return new RecommendationsResponse(userId, recommendations);
    }
}