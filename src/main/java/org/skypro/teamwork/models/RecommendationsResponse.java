package org.skypro.teamwork.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecommendationsResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("recommendations")
    private List<Recommendation> recommendation;

    public RecommendationsResponse() {}

    public RecommendationsResponse(Long userId, List<Recommendation> recommendations) {
        this.userId = userId;
        this.recommendation = recommendations;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Recommendation> getRecommendations() {
        return recommendation;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendation = recommendations;
    }
}
