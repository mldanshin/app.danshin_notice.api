package net.danshin.danshin_notice_api.repositories.impl;

import net.danshin.danshin_notice_api.exceptoins.DataNotFoundException;
import net.danshin.danshin_notice_api.exceptoins.ServerException;
import net.danshin.danshin_notice_api.repositories.RecipientRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;

@Primary
@Repository
@SuppressWarnings("unused")
public class RecipientRepositoryImpl implements RecipientRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecipientRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createRecipient(String uuidUser) {
        try {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO recipients (uuid) VALUES (?);",
                    (PreparedStatement ps) -> ps.setString(1, uuidUser));
        } catch (Exception _) {
            throw new ServerException("An unexpected error occurred while creating the recipient");
        }
    }

    @Transactional
    @Override
    public void deleteRecipient(String uuidUser) {
        int affectedRows = jdbcTemplate.update(
                "DELETE FROM recipients WHERE uuid = ?;",
                (PreparedStatement ps) -> ps.setString(1, uuidUser));

        if (affectedRows == 0) {
            throw new DataNotFoundException("Recipient not found");
        }
    }

    @Override
    public Integer existRecipient(String uuidUser) {
        return jdbcTemplate.query(
                "SELECT id FROM recipients WHERE uuid = ?",
                ps -> ps.setString(1, uuidUser),
                rs -> {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                    return null;
                }
        );
    }
}
