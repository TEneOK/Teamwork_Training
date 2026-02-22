package org.skypro.teamwork.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Recommendation {

    @JsonProperty("id")
    private UUID productId;

    @JsonProperty("name")
    private String productName;

    @JsonProperty("text")
    private String description;

    public Recommendation() {}

    public Recommendation(UUID productId, String productName, String description) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}