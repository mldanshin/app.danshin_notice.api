package net.danshin.danshin_notice_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.danshin.danshin_notice_api.TestConstants;
import net.danshin.danshin_notice_api.config.TestJacksonConfig;
import net.danshin.danshin_notice_api.models.recipient.RecipientDeleteRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestJacksonConfig.class)
public class RecipientControllerTest {

    private final MockMvc mockMvc;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecipientControllerTest(MockMvc mockMvc, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    private static Stream<Arguments> delete403() {
        return Stream.of(
                Arguments.of(
                        new RecipientDeleteRequest("29c9ce14-1fed-42ea-8851-0b0f7db2c4da")
                )
        );
    }

    private static Stream<Arguments> delete404() {
        return Stream.of(
                Arguments.of(
                        new RecipientDeleteRequest("ff3e4507-e00b-12d3-a456-426614174000")
                )
        );
    }

    private static Stream<Arguments> delete200() {
        return Stream.of(
                Arguments.of(
                        new RecipientDeleteRequest(TestConstants.TRUSTED_UUID_2)
                ),
                Arguments.of(
                        new RecipientDeleteRequest(TestConstants.ADMIN_UUID)
                ),
                Arguments.of(
                        new RecipientDeleteRequest(TestConstants.RELATIVE_UUID_2)
                )
        );
    }

    @Test
    void create201() throws Exception {
        mockMvc.perform(post("/api/recipients/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().is(201));

        Integer countAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM recipients WHERE uuid = ?",
                Integer.class,
                TestConstants.TRUSTED_UUID);
        assertEquals(1, countAfter, "");
    }

    @Test
    void create401() throws Exception {
        mockMvc.perform(post("/api/recipients/create"))
                .andExpect(status().is(401));
    }

    @Test
    void create403() throws Exception {
        mockMvc.perform(post("/api/recipients/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().is(403));
    }

    @ParameterizedTest
    @MethodSource("delete200")
    void delete200(RecipientDeleteRequest request) throws Exception {
        mockMvc.perform(post("/api/recipients/delete")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(204));

        Integer countAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM recipients WHERE uuid = ?",
                Integer.class,
                request.uuid());
        assertEquals(0, countAfter, "Recipient should be deleted from database");

        Integer channelCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM channels WHERE recipient_id = (SELECT id FROM recipients WHERE uuid = ?)",
                Integer.class,
                request.uuid()
        );
        assertEquals(0, channelCount, "All channels should be deleted");
    }

    @ParameterizedTest
    @MethodSource("delete403")
    void delete401(RecipientDeleteRequest request) throws Exception {
        mockMvc.perform(post("/api/recipients/delete")
                        .header("X-API-Key", "FF-jPf6xlA5-SOHntd0ukcHKGChL7fWp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(401));
    }

    @ParameterizedTest
    @MethodSource("delete403")
    void delete403(RecipientDeleteRequest request) throws Exception {
        mockMvc.perform(post("/api/recipients/delete")
                        .header("X-API-Key", "eHsSIrPK1NR3EYqE6gVsNQd1MbFvs7Io")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(403));
    }

    @ParameterizedTest
    @MethodSource("delete404")
    void delete404(RecipientDeleteRequest request) throws Exception {
        mockMvc.perform(post("/api/recipients/delete")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(404));
    }
}
