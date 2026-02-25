package org.skypro.teamwork.controller;

import org.skypro.teamwork.dto.DynamicRuleDto;
import org.skypro.teamwork.dto.DynamicRuleListResponse;
import org.skypro.teamwork.dto.RuleStatsResponse;
import org.skypro.teamwork.service.DynamicRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
public class DynamicRuleController {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRuleController.class);
    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @PostMapping
    public ResponseEntity<DynamicRuleDto> createRule(@Valid @RequestBody DynamicRuleDto ruleDto) {
        logger.info("Creating new dynamic rule for product: {}", ruleDto.getProductName());

        try {
            DynamicRuleDto createdRule = dynamicRuleService.createRule(ruleDto);
            return ResponseEntity.status(HttpStatus.OK).body(createdRule);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create rule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Error creating rule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<DynamicRuleListResponse> getAllRules() {
        logger.info("Fetching all dynamic rules");

        try {
            var rules = dynamicRuleService.getAllRules();  // ТЕПЕРЬ ЭТОТ МЕТОД ЕСТЬ
            return ResponseEntity.ok(new DynamicRuleListResponse(rules));
        } catch (Exception e) {
            logger.error("Error fetching rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<RuleStatsResponse> getRuleStats() {
        logger.info("Fetching rule statistics");

        try {
            var stats = dynamicRuleService.getRuleStats();
            return ResponseEntity.ok(new RuleStatsResponse(stats));
        } catch (Exception e) {
            logger.error("Error fetching rule statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{product_id}")
    public ResponseEntity<Void> deleteRule(@PathVariable("product_id") UUID productId) {
        logger.info("Deleting rule for product: {}", productId);

        try {
            dynamicRuleService.deleteRule(productId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Rule not found: {}", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error deleting rule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}