package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.GenealogyResponse;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class GenealogyDatesUpcomingServiceTest {

    private final GenealogyDatesUpcomingService genealogyDatesUpcomingService;

    @Autowired
    public GenealogyDatesUpcomingServiceTest(
            GenealogyDatesUpcomingService genealogyDatesUpcomingService) {
        this.genealogyDatesUpcomingService = genealogyDatesUpcomingService;
    }

    @Test
    void getRecipients_shouldReturnAllRecipients() {
        List<Recipient> recipients = genealogyDatesUpcomingService.getRecipients();

        assertNotNull(recipients);
        assertEquals(3, recipients.size());

        Recipient recipient2 = recipients.stream()
                .filter(r -> r.id() == 2)
                .findFirst()
                .orElse(null);
        assertNotNull(recipient2);
        assertEquals(LocalTime.of(9, 34, 11), recipient2.sendTime());
        assertEquals("Asia/Dubai", recipient2.timezone());
        assertEquals(1, recipient2.daysBefore());
        assertEquals(1, recipient2.daysAfter());
        assertEquals("admin@example.com", recipient2.email());
        assertEquals("tgadmin", recipient2.telegramChatId());
        assertNotNull(recipient2.personDate());
        assertEquals(6, recipient2.personDate().size());

        Recipient recipient3 = recipients.stream()
                .filter(r -> r.id() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(recipient3);
        assertEquals(LocalTime.of(23, 12, 1), recipient3.sendTime());
        assertEquals("Europe/Moscow", recipient3.timezone());
        assertEquals(2, recipient3.daysBefore());
        assertEquals(3, recipient3.daysAfter());
        assertNull(recipient3.email());
        assertEquals("tgrelative", recipient3.telegramChatId());
        assertNotNull(recipient3.personDate());
        assertEquals(7, recipient3.personDate().size());

        boolean recipient1Exists = recipients.stream().anyMatch(r -> r.id() == 1);
        boolean recipient4Exists = recipients.stream().anyMatch(r -> r.id() == 4);
        assertFalse(recipient1Exists);
        assertFalse(recipient4Exists);
    }

    @Test
    void getRecipients_shouldReturnPersonDatesForRecipient() {
        List<Recipient> recipients = genealogyDatesUpcomingService.getRecipients();

        Recipient recipient = recipients.stream()
                .filter(r -> r.id() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(recipient);
        assertNotNull(recipient.personDate());
        assertFalse(recipient.personDate().isEmpty());

        long birthCount = recipient.personDate().stream()
                .filter(pd -> pd.dateType() == DateType.BIRTH)
                .count();
        long deathCount = recipient.personDate().stream()
                .filter(pd -> pd.dateType() == DateType.DEATH)
                .count();

        assertEquals(4, birthCount);
        assertEquals(3, deathCount);
    }

    @Test
    void getGenealogyData_shouldReturnNullWhenRecipientHasNoPersons() {
        LocalDate date = LocalDate.of(2026, 8, 15);
        Recipient recipient = new Recipient(
                999,
                LocalTime.of(12, 0),
                "UTC",
                1,
                1,
                null,
                null,
                null
        );

        GenealogyResponse response = genealogyDatesUpcomingService.getGenealogyData(date, recipient);

        assertNull(response);
    }
}