package org.skypro.teamwork.controller;

import org.skypro.teamwork.models.RecommendationsResponse;  // Обратите внимание: models -> model
import org.skypro.teamwork.service.RecommendationService;
import org.skypro.teamwork.exception.UserNotFoundException;
import org.skypro.teamwork.exception.InvalidUserIdException;
import org.skypro.teamwork.exception.RecommendationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.Collections;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping(value = "/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationsResponse> getRecommendation(
            @PathVariable("user_id") UUID userId) {

        logger.info("Получен запрос на рекомендации, userId: {}", userId);

        try {
            RecommendationsResponse response =
                    recommendationService.getRecommendationsForUser(userId);

            logger.debug("Получено успешно {} рекомендации для пользователя {}",
                    response.getRecommendations().size(), userId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (InvalidUserIdException e) {
            logger.warn("Неверный формат ID: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RecommendationsResponse(userId, Collections.emptyList()));

        } catch (UserNotFoundException e) {
            logger.info("Пользователь не найден: {}", userId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new RecommendationsResponse(userId, Collections.emptyList()));

        } catch (RecommendationServiceException e) {
            logger.error("Ошибка сервиса при обработке рекомендаций для пользователя: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RecommendationsResponse(userId, Collections.emptyList()));

        } catch (Exception e) {
            logger.error("Непредвиденная ошибка для пользователя: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RecommendationsResponse(userId, Collections.emptyList()));
        }
    }
}