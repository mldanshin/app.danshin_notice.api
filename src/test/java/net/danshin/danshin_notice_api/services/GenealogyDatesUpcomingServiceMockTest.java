package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenealogyDatesUpcomingServiceMockTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GenealogyDatesUpcomingService service;

    @Test
    void getGenealogyData_shouldFilterBirthEvents() {
        LocalDate date = LocalDate.of(2026, 8, 15);

        Recipient recipient = new Recipient(
                1,
                LocalTime.of(12, 0),
                "UTC",
                12,
                10,
                List.of(
                        new PersonDate(2, DateType.BIRTH),
                        new PersonDate(18, DateType.BIRTH)
                ),
                null,
                null
        );

        GenealogyResponse mockResponse = createMockGenealogyResponse();
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GenealogyResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        GenealogyResponse result = service.getGenealogyData(date, recipient);

        assertThat(result).isNotNull();
        assertThat(result.beforeBirth()).hasSize(2);
        assertThat(result.beforeBirth().get(0).person().id()).isEqualTo(2);
        assertThat(result.beforeBirth().get(1).person().id()).isEqualTo(18);
        assertThat(result.beforeBirthWould()).hasSize(1);
        assertThat(result.beforeBirthWould().getFirst().person().id()).isEqualTo(2);
    }

    @Test
    void getGenealogyData_shouldFilterDeathEvents() {
        LocalDate date = LocalDate.of(2026, 8, 15);

        Recipient recipient = new Recipient(
                1,
                LocalTime.of(12, 0),
                "UTC",
                12,
                10,
                List.of(
                        new PersonDate(1, DateType.DEATH),
                        new PersonDate(17, DateType.DEATH)
                ),
                null,
                null
        );

        GenealogyResponse mockResponse = createMockGenealogyResponse();
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GenealogyResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        GenealogyResponse result = service.getGenealogyData(date, recipient);

        assertThat(result).isNotNull();
        assertThat(result.beforeDeath()).hasSize(2);
        assertThat(result.beforeDeath().get(0).person().id()).isEqualTo(17);
        assertThat(result.beforeDeath().get(1).person().id()).isEqualTo(1);
    }

    @Test
    void getGenealogyData_shouldReturnEmptyWhenNoMatchingEvents() {
        LocalDate date = LocalDate.of(2026, 8, 15);

        Recipient recipient = new Recipient(
                1,
                LocalTime.of(12, 0),
                "UTC",
                12,
                10,
                List.of(
                        new PersonDate(999, DateType.BIRTH)
                ),
                null,
                null
        );

        GenealogyResponse mockResponse = createMockGenealogyResponse();
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GenealogyResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        GenealogyResponse result = service.getGenealogyData(date, recipient);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void getGenealogyData_shouldReturnNullWhenRecipientHasNoPersons() {
        LocalDate date = LocalDate.of(2026, 8, 15);
        Recipient recipient = new Recipient(
                1,
                LocalTime.of(12, 0),
                "UTC",
                12,
                10,
                null,
                null,
                null
        );

        GenealogyResponse result = service.getGenealogyData(date, recipient);

        assertThat(result).isNull();
    }

    @Test
    void getGenealogyData_shouldReturnNullWhenApiReturnsNull() {
        LocalDate date = LocalDate.of(2026, 8, 15);

        Recipient recipient = new Recipient(
                1,
                LocalTime.of(12, 0),
                "UTC",
                12,
                10,
                List.of(
                        new PersonDate(2, DateType.BIRTH)
                ),
                null,
                null
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GenealogyResponse.class)
        )).thenReturn(null);

        GenealogyResponse result = service.getGenealogyData(date, recipient);

        assertThat(result).isNull();
    }

    private GenealogyResponse createMockGenealogyResponse() {
        Person person1 = new Person(1, "Иванов", null, "Иван", "Иванович");
        Person person2 = new Person(2, "Петров", null, "Петр", "Петрович");
        Person person17 = new Person(17, "Сидоров", null, "Сидор", "Сидорович");
        Person person18 = new Person(18, "Козлов", null, "Козьма", "Козьмич");

        Date date = new Date("2026", "08", "15");
        Age age = new Age(70, 0, 0);

        BirthEvent birthEvent1 = new BirthEvent(date, person2, age);
        BirthEvent birthEvent2 = new BirthEvent(date, person18, age);
        BirthEvent birthEvent3 = new BirthEvent(date, person17, age);

        BirthWouldEvent birthWouldEvent = new BirthWouldEvent(date, person2, age);

        DeathEvent deathEvent = new DeathEvent(date, person17, age, new Interval(1, 0, 0));
        DeathEvent deathEvent2 = new DeathEvent(date, person1, age, new Interval(2, 0, 0));

        return new GenealogyResponse(
                List.of(birthEvent1, birthEvent2, birthEvent3),
                List.of(birthWouldEvent),
                List.of(deathEvent, deathEvent2),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
}