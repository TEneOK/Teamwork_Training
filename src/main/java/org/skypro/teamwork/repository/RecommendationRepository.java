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
                            rs.getLong("product_id"),
                            rs.getString("product_name"),
                            rs.getString("description")
                    );
                }
            };

    @Autowired
    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
