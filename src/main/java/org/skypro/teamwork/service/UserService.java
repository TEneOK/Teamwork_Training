package org.skypro.teamwork.service;

import org.skypro.teamwork.models.Users;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Users> findUsersByName(String fullName) {
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        String sql = """
            SELECT id, first_name, last_name 
            FROM users 
            WHERE LOWER(first_name) LIKE LOWER(?) 
              AND LOWER(last_name) LIKE LOWER(?)
            """;

        return jdbcTemplate.query(sql, new UserRowMapper(),
                firstName + "%", lastName + "%");
    }

    private static class UserRowMapper implements RowMapper<Users> {
        @Override
        public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
            Users user = new Users();
            user.setId(UUID.fromString(rs.getString("id")));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            return user;
        }
    }
}