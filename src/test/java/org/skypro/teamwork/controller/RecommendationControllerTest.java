package org.skypro.teamwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.teamwork.TestDataFactory;
import org.skypro.teamwork.exception.InvalidUserIdException;
import org.skypro.teamwork.exception.UserNotFoundException;
import org.skypro.teamwork.exception.RecommendationServiceException;
import org.skypro.teamwork.models.RecommendationsResponse;
import org.skypro.teamwork.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
@DisplayName("Тесты контроллера рекомендаций")
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendationService recommendationService;

    private UUID validUserId;
    private UUID invalidUserId;
    private RecommendationsResponse testResponse;

    @BeforeEach
    void setUp() {
        validUserId = TestDataFactory.TEST_USER_ID;
        invalidUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        testResponse = TestDataFactory.createTestResponse();
    }

    @Test
    @DisplayName("Должен вернуть 200 OK с рекомендациями для существующего пользователя")
    void getRecommendations_WithValidUser_ShouldReturnOkAndRecommendations() throws Exception {
        when(recommendationService.getRecommendationsForUser(validUserId))
                .thenReturn(testResponse);

        mockMvc.perform(get("/recommendation/{user_id}", validUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(validUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations.length()").value(2))
                .andExpect(jsonPath("$.recommendations[0].id").exists())
                .andExpect(jsonPath("$.recommendations[0].name").exists())
                .andExpect(jsonPath("$.recommendations[0].text").exists());
    }

    @Test
    @DisplayName("Должен вернуть 400 Bad Request при невалидном UUID")
    void getRecommendations_WithInvalidUuid_ShouldReturnBadRequest() throws Exception {
        when(recommendationService.getRecommendationsForUser(any(UUID.class)))
                .thenThrow(new InvalidUserIdException("Невалидный ID"));

        mockMvc.perform(get("/recommendation/{user_id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());  // Spring автоматически вернет 400 для невалидного UUID
    }

    @Test
    @DisplayName("Должен вернуть 404 Not Found при отсутствии пользователя")
    void getRecommendations_WithNonExistentUser_ShouldReturnNotFound() throws Exception {
        when(recommendationService.getRecommendationsForUser(invalidUserId))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/recommendation/{user_id}", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.user_id").value(invalidUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations.length()").value(0));
    }

    @Test
    @DisplayName("Должен вернуть 500 Internal Server Error при ошибке сервиса")
    void getRecommendations_WhenServiceError_ShouldReturnInternalServerError() throws Exception {
        when(recommendationService.getRecommendationsForUser(validUserId))
                .thenThrow(new RecommendationServiceException("Ошибка БД"));

        mockMvc.perform(get("/recommendation/{user_id}", validUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.user_id").value(validUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations.length()").value(0));
    }

    @Test
    @DisplayName("Должен корректно обрабатывать null userId")
    void getRecommendations_WithNullUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/recommendation/{user_id}", "null")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}