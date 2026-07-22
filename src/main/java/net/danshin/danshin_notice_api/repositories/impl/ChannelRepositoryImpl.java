package net.danshin.danshin_notice_api.repositories.impl;

import net.danshin.danshin_notice_api.exceptoins.DataNotFoundException;
import net.danshin.danshin_notice_api.exceptoins.DataNotValidException;
import net.danshin.danshin_notice_api.models.channel.ChannelEmailResponse;
import net.danshin.danshin_notice_api.models.channel.ChannelTelegramResponse;
import net.danshin.danshin_notice_api.models.channel.ChannelsResponse;
import net.danshin.danshin_notice_api.repositories.ChannelRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
@Primary
@SuppressWarnings("unused")
public class ChannelRepositoryImpl implements ChannelRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RecipientRepositoryImpl recipientRepository;

    public ChannelRepositoryImpl(JdbcTemplate jdbcTemplate, RecipientRepositoryImpl recipientRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.recipientRepository = recipientRepository;
    }

    public ChannelsResponse getChannels(String recipientUuid) {
        Integer id = recipientRepository.existRecipient(recipientUuid);
        if (id == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        Map<String, Object> result = jdbcTemplate.query(
                "SELECT ced.email, ctd.chat_id, ctd.username " +
                        "FROM channels as c " +
                        "LEFT JOIN channels_email_details as ced ON c.id = ced.channel_id " +
                        "LEFT JOIN channels_telegram_details as ctd ON c.id = ctd.channel_id " +
                        "WHERE c.recipient_id = ?",
                (PreparedStatement ps) -> ps.setInt(1, id),
                rs -> {
                    String email = null;
                    String chatId = null;
                    String username = null;

                    while (rs.next()) {
                        if (rs.getString("email") != null) {
                            email = rs.getString("email");
                        }
                        if (rs.getString("chat_id") != null) {
                            chatId = rs.getString("chat_id");
                            username = rs.getString("username");
                        }
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("email", email);
                    data.put("chatId", chatId);
                    data.put("username", username);
                    return data;
                }
        );

        if (result == null) {
            throw new DataNotFoundException("Channels not found");
        }

        if (result.get("email") == null && result.get("chatId") == null) {
            throw new DataNotFoundException("Channels not found");
        }

        return new ChannelsResponse(
                result.get("email") != null ?
                        new ChannelEmailResponse((String) result.get("email")) : null,
                result.get("chatId") != null ?
                        new ChannelTelegramResponse((String) result.get("chatId"), (String) result.get("username")) : null
        );
    }

    @Override
    public ChannelEmailResponse getEmail(String recipientUuid) {
        Integer id = recipientRepository.existRecipient(recipientUuid);
        if (id == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        ChannelEmailResponse response = jdbcTemplate.query(
                "SELECT ced.email FROM channels as c " +
                        "JOIN channels_email_details as ced ON c.id = ced.channel_id " +
                        "WHERE c.recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, id),
                rs -> {
                    if (rs.next()) {
                        return new ChannelEmailResponse(
                                rs.getString("email")
                        );
                    }
                    return null;
                }
        );

        if (response == null) {
            throw new DataNotFoundException("Channel email not found");
        }

        return response;
    }

    @Override
    public ChannelTelegramResponse getTelegram(String recipientUuid) {
        Integer id = recipientRepository.existRecipient(recipientUuid);
        if (id == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        ChannelTelegramResponse response = jdbcTemplate.query(
                "SELECT ced.chat_id, ced.username FROM channels as c " +
                        "JOIN channels_telegram_details as ced ON c.id = ced.channel_id " +
                        "WHERE c.recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, id),
                rs -> {
                    if (rs.next()) {
                        return new ChannelTelegramResponse(
                                rs.getString("chat_id"),
                                rs.getString("username")
                        );
                    }
                    return null;
                }
        );

        if (response == null) {
            throw new DataNotFoundException("Channel telegram for recipient " + recipientUuid + " not found");
        }

        return response;
    }

    @Override
    @Transactional
    public void createTelegram(String recipientUuid, String chatId, String username) {
        Integer idRecipient = recipientRepository.existRecipient(recipientUuid);
        if (idRecipient == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        jdbcTemplate.update(
                "DELETE FROM channels WHERE recipient_id = ? AND type_id = 2",
                idRecipient
        );

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO channels (type_id, recipient_id) " +
                            "VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, 2);
            ps.setInt(2, idRecipient);
            return ps;
        }, keyHolder);

        int idChannel = Objects.requireNonNull(keyHolder.getKey()).intValue();

        jdbcTemplate.update(
                "INSERT INTO channels_telegram_details (channel_id, chat_id, username) " +
                        "VALUES (?, ?, ?)",
                (ps) -> {
                    ps.setInt(1, idChannel);
                    ps.setString(2, chatId);
                    ps.setString(3, username);
                }
        );

        createGenealogyDate(idRecipient, idChannel);
    }

    @Override
    @Transactional
    public void saveVerificationToken(String recipientUuid, String email, String token, int expiresInMinutes) {
        Integer recipientId = recipientRepository.existRecipient(recipientUuid);
        if (recipientId == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        jdbcTemplate.update(
                "DELETE FROM verification_email_tokens WHERE recipient_id = ?",
                recipientId
        );

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiresInMinutes);

        jdbcTemplate.update(
                "INSERT INTO verification_email_tokens (recipient_id, email, token, expires_at) " +
                        "VALUES (?, ?, ?, ?)",
                recipientId, email, token, Timestamp.valueOf(expiresAt)
        );
    }

    @Override
    @Transactional
    public void verifyEmailByToken(String recipientUuid, String token) {
        Integer recipientId = recipientRepository.existRecipient(recipientUuid);
        if (recipientId == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        Data data = jdbcTemplate.query(
                "SELECT recipient_id, email FROM verification_email_tokens " +
                        "WHERE token = ? AND expires_at > NOW()",
                (PreparedStatement ps) -> ps.setString(1, token),
                rs -> rs.next() ? new Data(rs.getInt("recipient_id"), rs.getString("email")) : null
        );

        if (data == null) {
            throw new DataNotFoundException("Token not found");
        }

        if (!data.id().equals(recipientId)) {
            throw new DataNotValidException("Recipient by token not equals recipient by uuid");
        }

        Integer genealogyId = jdbcTemplate.query(
                "SELECT id FROM genealogy_dates WHERE recipient_id = ?",
                (PreparedStatement ps) -> ps.setInt(1, recipientId),
                rs -> rs.next() ? rs.getInt("id") : null
        );

        int idChannel = createEmail(recipientId, data.email());

        if (genealogyId != null) {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO genealogy_dates_channels (genealogy_date_id, channel_id) VALUES (?, ?)",
                    genealogyId, idChannel
            );
        }

        jdbcTemplate.update(
                "DELETE FROM verification_email_tokens WHERE recipient_id = ?",
                data.id()
        );
    }

    public void deleteEmail(String recipientUuid, String email) {
        Integer recipientId = recipientRepository.existRecipient(recipientUuid);
        if (recipientId == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        int deleted = jdbcTemplate.update(
                "DELETE channels, channels_email_details " +
                        "FROM channels " +
                        "JOIN channels_email_details ON channels.id = channels_email_details.channel_id " +
                        "WHERE channels.recipient_id = ? AND channels_email_details.email = ?",
                recipientId,
                email
        );

        if (deleted == 0) {
            throw new DataNotFoundException("Email channel not found for this recipient");
        }
    }

    public void deleteTelegram(String recipientUuid, String chatId) {
        Integer recipientId = recipientRepository.existRecipient(recipientUuid);
        if (recipientId == null) {
            throw new DataNotFoundException("Recipient not found");
        }

        int deleted = jdbcTemplate.update(
                "DELETE channels, channels_telegram_details " +
                        "FROM channels " +
                        "JOIN channels_telegram_details ON channels.id = channels_telegram_details.channel_id " +
                        "WHERE channels.recipient_id = ? AND channels_telegram_details.chat_id = ?",
                recipientId,
                chatId
        );

        if (deleted == 0) {
            throw new DataNotFoundException("Email channel not found for this recipient");
        }
    }

    private int createEmail(int recipientId, String email) {
        jdbcTemplate.update(
                "REPLACE INTO channels (recipient_id, type_id) VALUES (?, ?)",
                recipientId, 1
        );

        Integer idChannel = jdbcTemplate.query(
                "SELECT id FROM channels WHERE recipient_id = ? AND type_id = 1",
                (PreparedStatement ps) -> ps.setInt(1, recipientId),
                rs -> rs.next() ? rs.getInt("id") : null
        );

        if (idChannel == null) {
            throw new RuntimeException("Failed to create channel for recipient: " + recipientId);
        }

        jdbcTemplate.update(
                "REPLACE INTO channels_email_details (channel_id, email) VALUES (?, ?)",
                idChannel, email
        );

        return idChannel;
    }

    private void createGenealogyDate(int idRecipient, int idChannel) {
        Integer id = jdbcTemplate.query(
                "SELECT id FROM genealogy_dates " +
                        "WHERE recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, idRecipient),
                rs ->
                        rs.next() ? rs.getInt("id") : null
        );

        if (id == null) {
            return;
        }

        jdbcTemplate.update(
                "INSERT INTO genealogy_dates_channels (genealogy_date_id, channel_id) " +
                        "VALUES (?, ?);",
                id, idChannel
        );
    }

    private record Data(Integer id, String email) {
    }
}
