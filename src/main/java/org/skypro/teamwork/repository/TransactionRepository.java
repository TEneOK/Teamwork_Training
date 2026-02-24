package org.skypro.teamwork.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final Cache<String, Boolean> existsCache;
    private final Cache<String, Integer> countCache;
    private final Cache<String, BigDecimal> sumCache;

    public TransactionRepository(@Qualifier("recommendationJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.existsCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10000)
                .build();

        this.countCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10000)
                .build();

        this.sumCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10000)
                .build();
    }

    public boolean existsByUserIdAndProductType(UUID userId, String productType) {
        String key = userId + "_" + productType;
        return existsCache.get(key, k -> {
            String sql = """
                SELECT COUNT(*) > 0
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
                """;
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                    sql, Boolean.class, userId.toString(), productType));
        });
    }

    public int countTransactionsByUserIdAndProductType(UUID userId, String productType) {
        String key = "count_" + userId + "_" + productType;
        return countCache.get(key, k -> {
            String sql = """
                SELECT COUNT(*)
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
                """;
            Integer count = jdbcTemplate.queryForObject(
                    sql, Integer.class, userId.toString(), productType);
            return count != null ? count : 0;
        });
    }

    public BigDecimal getSumByUserIdAndProductTypeAndTransactionType(
            UUID userId, String productType, String transactionType) {
        String key = "sum_" + userId + "_" + productType + "_" + transactionType;
        return sumCache.get(key, k -> {
            String sql = """
                SELECT COALESCE(SUM(t.amount), 0)
                FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ? AND t.type = ?
                """;
            BigDecimal sum = jdbcTemplate.queryForObject(
                    sql, BigDecimal.class, userId.toString(), productType, transactionType);
            return sum != null ? sum : BigDecimal.ZERO;
        });
    }
}