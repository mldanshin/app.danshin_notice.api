package net.danshin.danshin_notice_api.repositories.impl;

import net.danshin.danshin_notice_api.exceptoins.DataNotFoundException;
import net.danshin.danshin_notice_api.exceptoins.NoContentException;
import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;
import net.danshin.danshin_notice_api.models.genealogy_dates.PersonDate;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsRequest;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsResponse;
import net.danshin.danshin_notice_api.repositories.GenealogyDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Repository
@Primary
@SuppressWarnings("unused")
public class GenealogyDateRepositoryImpl implements GenealogyDateRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RecipientRepositoryImpl recipientRepository;

    @Autowired
    public GenealogyDateRepositoryImpl(JdbcTemplate jdbcTemplate, RecipientRepositoryImpl recipientRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.recipientRepository = recipientRepository;
    }

    @Override
    public SettingsResponse get(String uuid) {
        Integer id = recipientRepository.existRecipient(uuid);
        if (id == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        Response response = jdbcTemplate.query(
                "SELECT id, send_time, timezone, days_before, days_after " +
                        "FROM genealogy_dates " +
                        "WHERE recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, id),
                rs -> {
                    if (rs.next()) {
                        return new Response(
                                rs.getInt("id"),
                                rs.getTime("send_time").toLocalTime(),
                                rs.getString("timezone"),
                                rs.getInt("days_before"),
                                rs.getInt("days_after")
                        );
                    }
                    return null;
                }
        );

        if (response == null) {
            throw new NoContentException("Content recipient not found");
        }

        List<PersonDate> idPersons = jdbcTemplate.query(
                "SELECT person_id, type_id FROM `genealogy_dates_persons` " +
                        "WHERE genealogy_date_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, response.recipientId()),
                (ResultSet rs, int rowNum) -> {
                    try {
                        return new PersonDate(
                                rs.getInt("person_id"),
                                DateType.fromValue(rs.getInt("type_id"))
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return new SettingsResponse(
                uuid,
                response.sendTime(),
                response.timezone(),
                response.dayBefore(),
                response.dayAfter(),
                idPersons
        );
    }

    @Override
    @Transactional
    public void create(String uuidUser, SettingsRequest request) {
        Integer idRecipient = recipientRepository.existRecipient(uuidUser);
        if (idRecipient == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        jdbcTemplate.update("DELETE FROM genealogy_dates WHERE recipient_id = ?", idRecipient);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO genealogy_dates (recipient_id, send_time, timezone, days_before, days_after) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idRecipient);
            ps.setTime(2, Time.valueOf(request.sendTime()));
            ps.setString(3, request.timezone());
            ps.setInt(4, request.dayBefore());
            ps.setInt(5, request.dayAfter());
            return ps;
        }, keyHolder);

        int idGenDate = Objects.requireNonNull(keyHolder.getKey()).intValue();

        jdbcTemplate.batchUpdate(
                "INSERT INTO genealogy_dates_persons (genealogy_date_id, person_id, type_id) " +
                        "VALUES (?, ?, ?)",
                request.dates(),
                request.dates().size(),
                (ps, dates) -> {
                    ps.setInt(1, idGenDate);
                    ps.setInt(2, dates.personId());
                    ps.setInt(3, dates.dateType().getValue());
                }
        );

        createRelationshipChannel(idGenDate, idRecipient);
    }

    @Override
    @Transactional
    public void delete(String uuidUser) {
        Integer id = recipientRepository.existRecipient(uuidUser);
        if (id == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        jdbcTemplate.update(
                "DELETE FROM genealogy_dates WHERE recipient_id = ?",
                id
        );
    }

    public void createDate(int personId, DateType dateType) {
        jdbcTemplate.update(
                "INSERT IGNORE INTO genealogy_dates_persons " +
                        "(genealogy_date_id, person_id, type_id) SELECT id, ?, ? FROM genealogy_dates",
                ps -> {
                    ps.setInt(1, personId);
                    ps.setInt(2, dateType.getValue());
                }
        );
    }

    public void deletePerson(int id) {
        jdbcTemplate.update(
                "DELETE FROM genealogy_dates_persons WHERE person_id = ?",
                ps -> ps.setInt(1, id)
        );
    }

    private void createRelationshipChannel(int id, int idRecipient) {
        jdbcTemplate.update(
                "INSERT IGNORE INTO genealogy_dates_channels " +
                        "(genealogy_date_id, channel_id) SELECT ?, id FROM channels WHERE recipient_id = ?",
                ps -> {
                    ps.setInt(1, id);
                    ps.setInt(2, idRecipient);
                }
        );
    }

    private record Response(
            int recipientId,
            LocalTime sendTime,
            String timezone,
            int dayBefore,
            int dayAfter
    ) {
    }
}
