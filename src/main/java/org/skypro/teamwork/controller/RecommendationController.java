package org.skypro.teamwork.controller;
import org.skypro.teamwork.models.RecommendationsResponse;
import org.skypro.teamwork.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping(value = "/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationsResponse> getRecommendation(
            @PathVariable("user_id") Long userId) {

        try {
            RecommendationsResponse response =
                    recommendationService.getRecommendationsForUser(userId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new RecommendationsResponse(userId, java.util.Collections.emptyList()));
        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new RecommendationsResponse(userId, java.util.Collections.emptyList()));
        }
    }
}
