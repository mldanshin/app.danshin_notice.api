package net.danshin.danshin_notice_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.danshin.danshin_notice_api.TestConstants;
import net.danshin.danshin_notice_api.config.TestJacksonConfig;
import net.danshin.danshin_notice_api.models.channel.ChannelEmailRequest;
import net.danshin.danshin_notice_api.repositories.ChannelRepository;
import net.danshin.danshin_notice_api.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.PreparedStatement;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestJacksonConfig.class)
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ChannelControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final JdbcTemplate jdbcTemplate;
    private final ChannelRepository channelRepository;
    @MockitoBean
    @SuppressWarnings("unused")
    private NotificationService notificationService;

    @Autowired
    ChannelControllerTest(MockMvc mockMvc, ObjectMapper mapper, JdbcTemplate jdbcTemplate, ChannelRepository channelRepository) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
        this.channelRepository = channelRepository;
    }

    private static Stream<Arguments> requestEmailVerification400() {
        return Stream.of(
                Arguments.of(
                        new ChannelEmailRequest(
                                "userexample.com"
                        )
                ),
                Arguments.of(
                        new ChannelEmailRequest(
                                ""
                        )
                ),
                Arguments.of(
                        (Object) null
                )
        );
    }

    @Test
    void getChannels200() throws Exception {
        mockMvc.perform(get("/api/channel/channels")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email.email").value("admin@example.com"))
                .andExpect(jsonPath("$.telegram.chatId").value("tgadmin"))
                .andExpect(jsonPath("$.telegram.chatId").value("tgadmin"));
    }

    @Test
    void getChannels200_2() throws Exception {
        mockMvc.perform(get("/api/channel/channels")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(nullValue()))
                .andExpect(jsonPath("$.telegram.chatId").value("tgrelative"))
                .andExpect(jsonPath("$.telegram.chatId").value("tgrelative"));
    }

    @Test
    void getChannels401() throws Exception {
        mockMvc.perform(get("/api/channel/channels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getChannels403() throws Exception {
        mockMvc.perform(get("/api/channel/channels")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void getChannels404RecipientNotFound() throws Exception {
        mockMvc.perform(get("/api/channel/channels")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChannels404ChannelNotFound() throws Exception {
        mockMvc.perform(get("/api/channel/channels")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN_2))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEmail200() throws Exception {
        mockMvc.perform(get("/api/channel/email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    void getEmail401() throws Exception {
        mockMvc.perform(get("/api/channel/email"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEmail403() throws Exception {
        mockMvc.perform(get("/api/channel/email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void getEmail404RecipientNotFound() throws Exception {
        mockMvc.perform(get("/api/channel/email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEmail404ChannelNotFound() throws Exception {
        mockMvc.perform(get("/api/channel/email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN_2))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTelegram200() throws Exception {
        mockMvc.perform(get("/api/channel/telegram")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatId").value("tgadmin"))
                .andExpect(jsonPath("$.username").value("tgadmin"));
    }

    @Test
    void getTelegram401() throws Exception {
        mockMvc.perform(get("/api/channel/telegram"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTelegram403() throws Exception {
        mockMvc.perform(get("/api/channel/telegram")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTelegram404RecipientNotFound() throws Exception {
        mockMvc.perform(get("/api/channel/telegram")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTelegram404ChannelNotFound() throws Exception {
        mockMvc.perform(get("/api/channel/telegram")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN_2))
                .andExpect(status().isNotFound());
    }

    @Test
    void requestEmailVerification200() throws Exception {
        ChannelEmailRequest request = new ChannelEmailRequest(
                "admin@example.com"
        );

        mockMvc.perform(post("/api/channel/request-email-verification")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Integer id = jdbcTemplate.query(
                "SELECT recipient_id FROM verification_email_tokens " +
                        "WHERE email = ?",
                (PreparedStatement ps) -> ps.setString(1, request.email()),
                rs -> rs.next() ? rs.getInt("recipient_id") : null
        );
        assertEquals("", 2, id);
    }

    @ParameterizedTest
    @MethodSource("requestEmailVerification400")
    void requestEmailVerification400(ChannelEmailRequest request) throws Exception {
        mockMvc.perform(post("/api/channel/request-email-verification")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestEmailVerification401_NoToken() throws Exception {
        mockMvc.perform(post("/api/channel/request-email-verification"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestEmailVerification403_InvalidRole() throws Exception {
        ChannelEmailRequest request = new ChannelEmailRequest(
                "user@example.com"
        );
        mockMvc.perform(post("/api/channel/request-email-verification")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestEmailVerification404NotFoundRecipient() throws Exception {
        ChannelEmailRequest request = new ChannelEmailRequest(
                "trusted@example.com"
        );
        mockMvc.perform(post("/api/channel/request-email-verification")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void verifyEmailByLink200() throws Exception {
        String requestBody = "{\"token\": \"19d8915c-6fc6-486b-9571-b3f69e71dee8\"}";

        mockMvc.perform(post("/api/channel/verify-email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer id = jdbcTemplate.query(
                "SELECT id FROM verification_email_tokens " +
                        "WHERE id = ?",
                (PreparedStatement ps) -> ps.setInt(1, 1),
                rs -> rs.next() ? rs.getInt("id") : null
        );
        assertEquals("", null, id);

        String email = jdbcTemplate.query(
                "SELECT ced.email FROM channels as c " +
                        "JOIN channels_email_details as ced ON c.id = ced.channel_id " +
                        "WHERE c.recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, 3),
                rs -> rs.next() ? rs.getString("email") : null
        );
        assertEquals("", "relative@example.com", email);

        Integer channelId = jdbcTemplate.query(
                "SELECT ced.channel_id FROM channels as c " +
                        "JOIN channels_email_details as ced ON c.id = ced.channel_id " +
                        "WHERE c.recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, 3),
                rs -> rs.next() ? rs.getInt("channel_id") : null
        );
        if (channelId == null) {
            throw new Exception("Error test data");
        }

        Integer genDatesId = jdbcTemplate.query(
                "SELECT genealogy_date_id FROM genealogy_dates_channels WHERE channel_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, channelId),
                rs -> rs.next() ? rs.getInt("genealogy_date_id") : null
        );
        assertEquals("", 3, genDatesId);
    }

    @Test
    void verifyEmailByLink200NotGenDates() throws Exception {
        String requestBody = "{\"token\": \"ggd8915c-6fc6-486b-9571-b3f69e71dee8\"}";

        mockMvc.perform(post("/api/channel/verify-email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer id = jdbcTemplate.query(
                "SELECT id FROM verification_email_tokens " +
                        "WHERE id = ?",
                (PreparedStatement ps) -> ps.setInt(1, 2),
                rs -> rs.next() ? rs.getInt("id") : null
        );
        assertEquals("", null, id);

        String email = jdbcTemplate.query(
                "SELECT ced.email FROM channels as c " +
                        "JOIN channels_email_details as ced ON c.id = ced.channel_id " +
                        "WHERE c.recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, 4),
                rs -> rs.next() ? rs.getString("email") : null
        );
        assertEquals("", "trusted2@example.com", email);

        Integer channelId = jdbcTemplate.query(
                "SELECT ced.channel_id FROM channels as c " +
                        "JOIN channels_email_details as ced ON c.id = ced.channel_id " +
                        "WHERE c.recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, 4),
                rs -> rs.next() ? rs.getInt("channel_id") : null
        );
        if (channelId == null) {
            throw new Exception("Error test data");
        }

        Integer genDatesId = jdbcTemplate.query(
                "SELECT genealogy_date_id FROM genealogy_dates_channels WHERE channel_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, channelId),
                rs -> rs.next() ? rs.getInt("genealogy_date_id") : null
        );
        assertEquals("", null, genDatesId);
    }

    @Test
    void verifyEmailByLink400() throws Exception {
        mockMvc.perform(post("/api/channel/verify-email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmailByLink400StrangerToken() throws Exception {
        String requestBody = "{\"token\": \"19d8915c-6fc6-486b-9571-b3f69e71dee8\"}";
        mockMvc.perform(post("/api/channel/verify-email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmailByLink401() throws Exception {
        String requestBody = "{\"token\": \"19d8915c-6fc6-486b-9571-b3f69e71dee8\"}";
        mockMvc.perform(post("/api/channel/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verifyEmailByLink403() throws Exception {
        String requestBody = "{\"token\": \"19d8915c-6fc6-486b-9571-b3f69e71dee8\"}";
        mockMvc.perform(post("/api/channel/verify-email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void verifyEmailByLink404NotFoundRecipient() throws Exception {
        String requestBody = "{\"token\": \"19d8915c-6fc6-486b-9571-b3f69e71dee8\"}";
        mockMvc.perform(post("/api/channel/verify-email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void verifyEmailByLink404NotFoundToken() throws Exception {
        String requestBody = "{\"token\": \"ffd8915c-6fc6-486b-9571-b3f69e71dee8\"}";
        mockMvc.perform(post("/api/channel/verify-email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTelegramLink200() throws Exception {
        mockMvc.perform(get("/api/channel/telegram/link")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("https://t.me/DanshinGenealogyBot/?start=8eef2b34-f116-40ad-8d83-13d389b799c4"));
    }

    @Test
    void getTelegramLink401() throws Exception {
        mockMvc.perform(get("/api/channel/telegram/link"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTelegramLink403() throws Exception {
        mockMvc.perform(get("/api/channel/telegram/link")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void telegramWebhookStartCommandSuccess() throws Exception {
        String uuid = TestConstants.RELATIVE_UUID;
        String chatId = "123456789";
        String username = "testuser";

        String requestBody = """
                {
                    "message": {
                        "chat": {
                            "id": %s,
                            "username": "%s"
                        },
                        "text": "/start %s"
                    }
                }
                """.formatted(chatId, username, uuid);

        mockMvc.perform(post("/api/channel/webhook/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer idChannel = jdbcTemplate.query(
                "SELECT c.id FROM channels c " +
                        "JOIN channels_telegram_details ctd ON c.id = ctd.channel_id " +
                        "JOIN recipients r ON c.recipient_id = r.id " +
                        "WHERE r.uuid = ? AND ctd.chat_id = ?",
                (PreparedStatement ps) -> {
                    ps.setString(1, uuid);
                    ps.setString(2, chatId);
                },
                rs -> rs.next() ? rs.getInt("id") : null
        );

        if (idChannel == null) {
            throw new Exception("Error test data");
        }

        assertEquals("", 8, idChannel);

        String savedUsername = jdbcTemplate.queryForObject(
                "SELECT ctd.username FROM channels c " +
                        "JOIN channels_telegram_details ctd ON c.id = ctd.channel_id " +
                        "JOIN recipients r ON c.recipient_id = r.id " +
                        "WHERE r.uuid = ? AND ctd.chat_id = ?",
                String.class,
                uuid, chatId
        );

        assertEquals("Username должен сохраниться", username, savedUsername);

        Integer genDatesId = jdbcTemplate.query(
                "SELECT genealogy_date_id FROM genealogy_dates_channels WHERE channel_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, idChannel),
                rs ->
                        rs.next() ? rs.getInt("genealogy_date_id") : null
        );
        assertEquals("", 3, genDatesId);
    }

    @Test
    void telegramWebhookWithoutStartCommand() throws Exception {
        String chatId = "987654321";
        String username = "anotheruser";

        String requestBody = """
                {
                    "message": {
                        "chat": {
                            "personDate": %s,
                            "username": "%s"
                        },
                        "text": "Hello, bot!"
                    }
                }
                """.formatted(chatId, username);

        mockMvc.perform(post("/api/channel/webhook/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels_telegram_details WHERE chat_id = ?",
                Integer.class,
                chatId
        );

        assertEquals("Канал не должен создаться без команды /start", 0, count);
    }

    @Test
    void telegramWebhookStartWithoutUuid() throws Exception {
        String chatId = "555555555";
        String username = "nouser";

        String requestBody = """
                {
                    "message": {
                        "chat": {
                            "personDate": %s,
                            "username": "%s"
                        },
                        "text": "/start"
                    }
                }
                """.formatted(chatId, username);

        mockMvc.perform(post("/api/channel/webhook/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels_telegram_details WHERE chat_id = ?",
                Integer.class,
                chatId
        );

        assertEquals("Канал не должен создаться без UUID", 0, count);
    }

    @Test
    void telegramWebhookWithInvalidUuid() throws Exception {
        String chatId = "777777777";
        String username = "invaliduser";
        String invalidUuid = "invalid-uuid-123";

        String requestBody = """
                {
                    "message": {
                        "chat": {
                            "personDate": %s,
                            "username": "%s"
                        },
                        "text": "/start %s"
                    }
                }
                """.formatted(chatId, username, invalidUuid);

        mockMvc.perform(post("/api/channel/webhook/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels_telegram_details WHERE chat_id = ?",
                Integer.class,
                chatId
        );

        assertEquals("Канал не должен создаться с невалидным UUID", 0, count);
    }

    @Test
    void telegramWebhookReplacesOldChatId() throws Exception {
        String uuid = "8eef2b34-f116-40ad-8d83-13d389b799c4";
        String oldChatId = "111111111";
        String newChatId = "222222222";
        String username = "testuser";

        channelRepository.createTelegram(uuid, oldChatId, username);

        Integer countBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels_telegram_details WHERE chat_id = ?",
                Integer.class,
                oldChatId
        );
        assertEquals("Старый канал должен существовать", 1, countBefore);

        String requestBody = """
                {
                    "message": {
                        "chat": {
                            "id": %s,
                            "username": "%s"
                        },
                        "text": "/start %s"
                    }
                }
                """.formatted(newChatId, username, uuid);

        mockMvc.perform(post("/api/channel/webhook/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer countOld = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels_telegram_details WHERE chat_id = ?",
                Integer.class,
                oldChatId
        );
        assertEquals("Старый канал должен быть удален", 0, countOld);

        Integer countNew = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels_telegram_details WHERE chat_id = ?",
                Integer.class,
                newChatId
        );
        assertEquals("Новый канал должен быть создан", 1, countNew);
    }

    @Test
    void telegramWebhookWithoutMessageField() throws Exception {
        String requestBody = """
                {
                    "update_id": 123456
                }
                """;

        mockMvc.perform(post("/api/channel/webhook/telegram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEmail200() throws Exception {
        mockMvc.perform(delete("/api/channel/email" + "?email=admin@example.com")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isNoContent());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels c " +
                        "JOIN channels_email_details ctd ON c.id = ctd.channel_id " +
                        "WHERE ctd.email = 'admin@example.com'",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    void deleteEmail400() throws Exception {
        mockMvc.perform(delete("/api/channel/email")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEmail401() throws Exception {
        mockMvc.perform(delete("/api/channel/email"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteEmail403() throws Exception {
        mockMvc.perform(delete("/api/channel/email?email=1234567")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteEmail404RecipientNotFound() throws Exception {
        mockMvc.perform(delete("/api/channel/email?email=relative2@example.com")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEmail404ChannelNotFound() throws Exception {
        mockMvc.perform(delete("/api/channel/email?email=relative2@example.com")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTelegram200() throws Exception {
        mockMvc.perform(delete("/api/channel/telegram" + "?chatId=tgadmin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isNoContent());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels c " +
                        "JOIN channels_telegram_details ctd ON c.id = ctd.channel_id " +
                        "WHERE ctd.chat_id = 'tgadmin'",
                Integer.class
        );
        assertThat(count).isZero();
    }

    @Test
    void deleteTelegram400() throws Exception {
        mockMvc.perform(delete("/api/channel/telegram")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTelegram401() throws Exception {
        mockMvc.perform(delete("/api/channel/telegram"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteTelegram403() throws Exception {
        mockMvc.perform(delete("/api/channel/telegram?chatId=1234567")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTelegram404RecipientNotFound() throws Exception {
        mockMvc.perform(delete("/api/channel/telegram?chatId=tgtrusted3")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTelegram404ChannelNotFound() throws Exception {
        mockMvc.perform(delete("/api/channel/telegram?chatId=tgtrusted3")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }
}
