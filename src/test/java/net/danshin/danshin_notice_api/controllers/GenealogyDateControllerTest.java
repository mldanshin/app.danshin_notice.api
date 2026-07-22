package net.danshin.danshin_notice_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.danshin.danshin_notice_api.TestConstants;
import net.danshin.danshin_notice_api.config.TestJacksonConfig;
import net.danshin.danshin_notice_api.models.genealogy_dates.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.PreparedStatement;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestJacksonConfig.class)
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GenealogyDateControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    GenealogyDateControllerTest(MockMvc mockMvc, ObjectMapper mapper, JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
    }


    // ==================== GET TESTS ====================

    @Test
    void get200Admin() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(TestConstants.ADMIN_UUID))
                .andExpect(jsonPath("$.sendTime").value("09:34:11"))
                .andExpect(jsonPath("$.timezone").value("Asia/Dubai"))
                .andExpect(jsonPath("$.dayBefore").value(1))
                .andExpect(jsonPath("$.dayAfter").value(1))
                .andExpect(jsonPath("$.dates", hasSize(6)));
    }

    @Test
    void get200Relative() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(TestConstants.RELATIVE_UUID))
                .andExpect(jsonPath("$.dates", hasSize(7)));
    }

    @Test
    void get204() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN_2))
                .andExpect(status().isNoContent());
    }

    @Test
    void get404() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void get401() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void get403() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    // ==================== CREATE TESTS ====================

    @Test
    void create201() throws Exception {
        SettingsRequest request = new SettingsRequest(
                LocalTime.of(10, 30, 0),
                "Europe/Moscow",
                5,
                7,
                List.of(
                        new PersonDate(100, DateType.BIRTH),
                        new PersonDate(200, DateType.DEATH),
                        new PersonDate(300, DateType.BIRTH)
                )
        );

        mockMvc.perform(post("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN_2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(TestConstants.TRUSTED_UUID_2))
                .andExpect(jsonPath("$.sendTime").value("10:30:00"))
                .andExpect(jsonPath("$.dayBefore").value(5))
                .andExpect(jsonPath("$.dayAfter").value(7))
                .andExpect(jsonPath("$.dates", hasSize(3)));

        Integer genDatesId = jdbcTemplate.query(
                "SELECT genealogy_date_id FROM genealogy_dates_channels as gdc " +
                        "JOIN genealogy_dates as gd ON gd.id = gdc.genealogy_date_id " +
                        "WHERE recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, 1),
                rs -> rs.next() ? rs.getInt("genealogy_date_id") : null
        );
        assertEquals("", null, genDatesId);
    }

    @Test
    void create201_2() throws Exception {
        SettingsRequest request = new SettingsRequest(
                LocalTime.of(10, 30, 0),
                "Europe/Moscow",
                5,
                7,
                List.of(
                        new PersonDate(100, DateType.BIRTH),
                        new PersonDate(200, DateType.DEATH),
                        new PersonDate(300, DateType.BIRTH)
                )
        );

        mockMvc.perform(post("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN_2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(TestConstants.RELATIVE_UUID_2))
                .andExpect(jsonPath("$.sendTime").value("10:30:00"))
                .andExpect(jsonPath("$.dayBefore").value(5))
                .andExpect(jsonPath("$.dayAfter").value(7))
                .andExpect(jsonPath("$.dates", hasSize(3)));

        Integer genDatesId = jdbcTemplate.query(
                "SELECT genealogy_date_id FROM genealogy_dates_channels as gdc " +
                        "JOIN genealogy_dates as gd ON gd.id = gdc.genealogy_date_id " +
                        "WHERE recipient_id = ?;",
                (PreparedStatement ps) -> ps.setInt(1, 4),
                rs -> rs.next() ? rs.getInt("genealogy_date_id") : null
        );
        assertEquals("", 6, genDatesId);
    }

    @Test
    void create401() throws Exception {
        mockMvc.perform(post("/api/genealogy_dates")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create403() throws Exception {
        SettingsRequest request = new SettingsRequest(
                LocalTime.of(14, 20, 0),
                "Europe/Moscow",
                3,
                5,
                List.of(
                        new PersonDate(10, DateType.BIRTH),
                        new PersonDate(20, DateType.BIRTH),
                        new PersonDate(30, DateType.DEATH)
                )
        );
        mockMvc.perform(post("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create404() throws Exception {
        SettingsRequest request = new SettingsRequest(
                LocalTime.of(10, 30, 0),
                "Europe/Moscow",
                5,
                7,
                List.of(
                        new PersonDate(100, DateType.BIRTH),
                        new PersonDate(200, DateType.DEATH),
                        new PersonDate(300, DateType.BIRTH)
                )
        );

        mockMvc.perform(post("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void delete204() throws Exception {
        mockMvc.perform(delete("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete401() throws Exception {
        mockMvc.perform(delete("/api/genealogy_dates"))
                .andExpect(status().is(401));
    }

    @Test
    void delete403() throws Exception {
        mockMvc.perform(delete("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().is(403));
    }

    @Test
    void delete404() throws Exception {
        mockMvc.perform(delete("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isNotFound());
    }

    // ==================== HANDLE PERSON CREATED TESTS ====================

    @Test
    void handlePersonCreated_ShouldReturn201_CreatesNewPerson() throws Exception {
        int newPersonId = 999;
        DateType dateType = DateType.BIRTH;
        PersonDateCreatedRequest request = new PersonDateCreatedRequest(newPersonId, dateType);

        mockMvc.perform(post("/api/genealogy_dates/events/person-created")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(8)))
                .andExpect(jsonPath("$.dates[?(@.personId == " + newPersonId + ")]").exists());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(7)))
                .andExpect(jsonPath("$.dates[?(@.personId == " + newPersonId + ")]").exists());
    }

    @Test
    void handlePersonCreated_ShouldReturn201_WhenPersonAlreadyExistsInSomeGenealogyDates() throws Exception {
        int existingPersonId = 18;
        DateType dateType = DateType.BIRTH;
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.personId == " + existingPersonId + ")]").exists());

        PersonDateCreatedRequest request = new PersonDateCreatedRequest(existingPersonId, dateType);
        mockMvc.perform(post("/api/genealogy_dates/events/person-created", existingPersonId)
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(7)))
                .andExpect(jsonPath("$.dates[?(@.personId == " + existingPersonId + ")]").exists());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(6)))
                .andExpect(jsonPath("$.dates[?(@.personId == " + existingPersonId + ")]").exists());
    }

    @Test
    void handlePersonCreated_ShouldReturn201_WhenPersonAlreadyNoExistsInSomeGenealogyDates() throws Exception {
        int existingPersonId = 18;
        DateType dateType = DateType.DEATH;
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.personId == " + existingPersonId + ")]").exists());

        PersonDateCreatedRequest request = new PersonDateCreatedRequest(existingPersonId, dateType);
        mockMvc.perform(post("/api/genealogy_dates/events/person-created", existingPersonId)
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(8)))
                .andExpect(jsonPath("$.dates[?(@.personId == " + existingPersonId + ")]").exists());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(7)))
                .andExpect(jsonPath("$.dates[?(@.personId == " + existingPersonId + ")]").exists());
    }

    @Test
    void handlePersonCreated_ShouldReturn201_WhenPersonDoesNotExistInAnyGenealogyDates() throws Exception {
        int newPersonId = 777;
        DateType dateType = DateType.DEATH;

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.id == " + newPersonId + ")]").doesNotExist());

        PersonDateCreatedRequest request = new PersonDateCreatedRequest(newPersonId, dateType);
        mockMvc.perform(post("/api/genealogy_dates/events/person-created", newPersonId)
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.personId == " + newPersonId + ")]").exists());
    }

    @Test
    void handlePersonCreated_ShouldReturn201_WhenPersonExistsInAllGenealogyDates() throws Exception {
        int personId = 15;
        DateType dateType = DateType.BIRTH;

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.personId == " + personId + ")]").exists());

        PersonDateCreatedRequest request = new PersonDateCreatedRequest(personId, dateType);
        mockMvc.perform(post("/api/genealogy_dates/events/person-created", personId)
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(7)));
    }

    @Test
    void handlePersonCreated_ShouldReturn401_WhenNoToken() throws Exception {
        PersonDateCreatedRequest request = new PersonDateCreatedRequest(999, DateType.BIRTH);
        mockMvc.perform(post("/api/genealogy_dates/events/person-created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void handlePersonCreated_ShouldReturn403_WhenUserIsRelative() throws Exception {
        PersonDateCreatedRequest request = new PersonDateCreatedRequest(999, DateType.BIRTH);
        mockMvc.perform(post("/api/genealogy_dates/events/person-created")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void handlePersonCreated_ShouldReturn403_WhenUserIsUser() throws Exception {
        PersonDateCreatedRequest request = new PersonDateCreatedRequest(999, DateType.BIRTH);
        mockMvc.perform(post("/api/genealogy_dates/events/person-created")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void handlePersonCreated_ShouldReturn400_WhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/genealogy_dates/events/person-created")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handlePersonCreated_ShouldReturn400_WhenRequestBodyHasNullId() throws Exception {
        String jsonRequest = "{}";
        mockMvc.perform(post("/api/genealogy_dates/events/person-created")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    // ==================== HANDLE PERSON DELETED TESTS ====================

    @Test
    void handlePersonDeleted_ShouldReturn204_DeletesExistingPerson() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(7)))
                .andExpect(jsonPath("$.dates[?(@.personId == 18)]").exists());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(6)))
                .andExpect(jsonPath("$.dates[?(@.personId == 18)]").exists());

        PersonDateDeletedRequest request = new PersonDateDeletedRequest(18);
        mockMvc.perform(post("/api/genealogy_dates/events/person-deleted", 18)
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(6)))
                .andExpect(jsonPath("$.dates[?(@.id == 18)]").doesNotExist());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates", hasSize(5)))
                .andExpect(jsonPath("$.dates[?(@.id == 18)]").doesNotExist());
    }

    @Test
    void handlePersonDeleted_ShouldReturn204_WhenDeletingPersonWithSingleEntry() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.personId == 1)]").exists());

        PersonDateDeletedRequest request = new PersonDateDeletedRequest(1);
        mockMvc.perform(post("/api/genealogy_dates/events/person-deleted")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.id == 1)]").doesNotExist());
    }

    @Test
    void handlePersonDeleted_ShouldReturn204_WhenDeletingNonExistentPerson() throws Exception {
        PersonDateDeletedRequest request = new PersonDateDeletedRequest(999);
        mockMvc.perform(post("/api/genealogy_dates/events/person-deleted")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void handlePersonDeleted_ShouldReturn204_WhenDeletingPersonWithMultipleEntries() throws Exception {
        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.personId == 15)]").exists());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.personId == 15)]").exists());

        PersonDateDeletedRequest request = new PersonDateDeletedRequest(15);
        mockMvc.perform(post("/api/genealogy_dates/events/person-deleted")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/genealogy_dates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.RELATIVE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.id == 15)]").doesNotExist());

        mockMvc.perform(get("/api/genealogy_dates", TestConstants.ADMIN_UUID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dates[?(@.id == 15)]").doesNotExist());
    }

    @Test
    void handlePersonDeleted_ShouldReturn401_WhenNoToken() throws Exception {
        PersonDateDeletedRequest request = new PersonDateDeletedRequest(18);
        mockMvc.perform(post("/api/genealogy_dates/events/person-deleted")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void handlePersonDeleted_ShouldReturn403_WhenUserRole() throws Exception {
        PersonDateDeletedRequest request = new PersonDateDeletedRequest(18);
        mockMvc.perform(post("/api/genealogy_dates/events/person-deleted")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void handlePersonDeleted_ShouldReturn400_WhenRequestBodyIsNull() throws Exception {
        mockMvc.perform(post("/api/genealogy_dates/events/person-deleted")
                        .header("X-API-Key", TestConstants.ADMIN_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
