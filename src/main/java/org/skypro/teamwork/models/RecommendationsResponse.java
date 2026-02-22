package org.skypro.teamwork.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.deser.jdk.UUIDDeserializer;

import java.util.List;
import java.util.UUID;

public class RecommendationsResponse {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("recommendations")
    private List<Recommendation> recommendation;

    public RecommendationsResponse() {}

    public RecommendationsResponse(UUID userId, List<Recommendation> recommendations) {
        this.userId = userId;
        this.recommendation = recommendations;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<Recommendation> getRecommendations() {
        return recommendation;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendation = recommendations;
    }
}
