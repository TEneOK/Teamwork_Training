package org.skypro.teamwork.repository;

import org.skypro.teamwork.models.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class RecommendationRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Recommendation> RECOMMENDATION_ROW_MAPPER =
            new RowMapper<Recommendation>() {
                @Override
                public Recommendation mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Recommendation(
                            UUID.fromString(rs.getString("product_id")),
                            rs.getString("product_name"),
                            rs.getString("description")
                    );
                }
            };

    @Autowired
    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Recommendation> findRecommendationsForUser(UUID userId) {
        String sql = """
        WITH user_data AS (
            SELECT 
                u.id as user_id,
                u.age,
                u.gender,
                u.income_level,
                COUNT(DISTINCT t.id) as transaction_count,
                COALESCE(SUM(t.amount), 0) as total_spent,
                MAX(t.transaction_date) as last_transaction_date
            FROM users u
            LEFT JOIN transactions t ON u.id = t.user_id
            WHERE u.id = ?
            GROUP BY u.id, u.age, u.gender, u.income_level
        ),
        -- Рекомендации на основе правил
        recommendations AS (
            -- По возрасту
            SELECT p.id as product_id, p.name as product_name, p.description
            FROM products p
            CROSS JOIN user_data ud
            WHERE (ud.age BETWEEN 18 AND 30 AND p.category = 'YOUTH')
               OR (ud.age BETWEEN 30 AND 50 AND p.category = 'PREMIUM')
            
            UNION
            
            -- По доходу
            SELECT p.id, p.name, p.description
            FROM products p
            CROSS JOIN user_data ud
            WHERE (ud.income_level = 'HIGH' AND p.price_range = 'HIGH')
               OR (ud.income_level = 'LOW' AND p.price_range = 'LOW')
            
            UNION
            
            -- Cross-sell на основе прошлых покупок
            SELECT p.id, p.name, p.description
            FROM user_data ud
            JOIN transactions t ON ud.user_id = t.user_id
            JOIN product_cross_sell pcs ON t.product_id = pcs.source_product_id
            JOIN products p ON pcs.target_product_id = p.id
            WHERE p.id NOT IN (
                SELECT product_id FROM transactions WHERE user_id = ud.user_id
            )
        )
        SELECT DISTINCT product_id, product_name, description 
        FROM recommendations
        LIMIT 50
        """;

        try {
            return jdbcTemplate.query(sql, RECOMMENDATION_ROW_MAPPER, userId.toString());
        } catch (Exception e) {
            System.err.println("Error fetching recommendations: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
