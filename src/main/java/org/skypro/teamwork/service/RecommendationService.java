package org.skypro.teamwork.service;

import org.skypro.teamwork.models.Recommendation;
import org.skypro.teamwork.models.RecommendationsResponse;
import org.skypro.teamwork.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Cacheable(value = "recommendations", key = "#userId")
    public RecommendationsResponse getRecommendationsForUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Неправильный ID");
        }

        List<Recommendation> recommendations =
                recommendationRepository.findRecommendationsForUser(userId);

        return new RecommendationsResponse(userId, recommendations);
    }
}