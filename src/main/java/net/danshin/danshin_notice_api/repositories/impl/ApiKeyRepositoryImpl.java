package net.danshin.danshin_notice_api.repositories.impl;

import net.danshin.danshin_notice_api.repositories.ApiKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
@SuppressWarnings("unused")
public class ApiKeyRepositoryImpl implements ApiKeyRepository {
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public ApiKeyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String findRoleByApiKey(String plainApiKey) {
        try {
            List<ApiKey> keys = jdbcTemplate.query(
                    "SELECT key_hash, role FROM api_keys",
                    (rs, rowNum) -> new ApiKey(
                            rs.getString("key_hash"),
                            rs.getString("role")
                    )
            );

            for (ApiKey key : keys) {
                if (BCrypt.checkpw(plainApiKey, key.keyHash())) {
                    return key.role();
                }
            }

            return null;
        } catch (DataAccessException e) {
            logger.error("Error finding role by API key", e);
            return null;
        }
    }

    private record ApiKey(String keyHash, String role) {
    }
}
