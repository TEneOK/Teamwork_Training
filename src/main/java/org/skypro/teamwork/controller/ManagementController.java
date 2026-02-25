package org.skypro.teamwork.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ManagementController.class);
    private final CacheManager cacheManager;

    public ManagementController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        logger.info("очистка всего кэша");

        try {
            cacheManager.getCacheNames().stream()
                    .forEach(cacheName -> {
                        logger.debug("Очистка кэша: {}", cacheName);
                        cacheManager.getCache(cacheName).clear();
                    });

            logger.info("Весь кэш был очищен");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Ошибка очистки кэша", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}