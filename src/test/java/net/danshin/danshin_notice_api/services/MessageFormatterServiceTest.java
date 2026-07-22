package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.helpers.MessageHelper;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class MessageFormatterServiceTest {

    @Mock
    private MessageHelper messageHelper;

    @InjectMocks
    private MessageFormatterService service;

    @Test
    void formatEmailMessage_shouldReturnNoEventsWhenResponseEmpty() {
        // given
        setupMessageHelperForTitleAndNoEvents();
        GenealogyResponse response = new GenealogyResponse(
                null, null, null, null, null, null, null, null, null, true
        );

        // when
        String result = service.formatEmailMessage(response);

        // then
        assertThat(result).contains("📅 События из родословной");
        assertThat(result).contains("Нет событий для отображения");
    }

    @Test
    void formatTelegramMessage_shouldReturnNoEventsWhenResponseEmpty() {
        // given
        setupMessageHelperForTitleAndNoEvents();
        GenealogyResponse response = new GenealogyResponse(
                null, null, null, null, null, null, null, null, null, true
        );

        // when
        String result = service.formatTelegramMessage(response);

        // then
        assertThat(result).contains("📅 События из родословной");
        assertThat(result).contains("Нет событий для отображения");
    }

    @Test
    void formatEmailMessage_shouldFormatBirthEvents() {
        // given
        setupMessageHelperForBirthEvents();
        GenealogyResponse response = createResponseWithBirthEvents();

        // when
        String result = service.formatEmailMessage(response);

        // then
        assertThat(result).contains("📅 События из родословной");
        assertThat(result).contains("Прошедшие");
        assertThat(result).contains("день рождения");
        assertThat(result).contains("Иванов Иван Иванович");
        assertThat(result).contains("исполнилось 70 лет");
        assertThat(result).contains("1956-08-13");
    }

    @Test
    void formatEmailMessage_shouldFormatBirthWouldEvents() {
        // given
        setupMessageHelperForBirthWouldEvents();
        GenealogyResponse response = createResponseWithBirthWouldEvents();

        // when
        String result = service.formatEmailMessage(response);

        // then
        assertThat(result).contains("Прошедшие");
        assertThat(result).contains("день рождения");
        assertThat(result).contains("Петров Петр Петрович");
        assertThat(result).contains("48 лет назад");
        assertThat(result).contains("1978-08-14");
    }

    @Test
    void formatEmailMessage_shouldFormatDeathEvents() {
        // given
        setupMessageHelperForDeathEvents();
        GenealogyResponse response = createResponseWithDeathEvents();

        // when
        String result = service.formatEmailMessage(response);

        // then
        assertThat(result).contains("Прошедшие");
        assertThat(result).contains("день памяти");
        assertThat(result).contains("Сидоров Сидор Сидорович");
        assertThat(result).contains("1 год назад");
        assertThat(result).contains("в возрасте");
        assertThat(result).contains("82 года");
        assertThat(result).contains("2025-08-16");
    }

    @Test
    void formatTelegramMessage_shouldFormatBirthEvents() {
        // given
        setupMessageHelperForBirthEvents();
        GenealogyResponse response = createResponseWithBirthEvents();

        // when
        String result = service.formatTelegramMessage(response);

        // then
        assertThat(result).contains("📅 События из родословной");
        assertThat(result).contains("Прошедшие:");
        assertThat(result).contains("день рождения");
        assertThat(result).contains("Иванов Иван Иванович");
        assertThat(result).contains("исполнилось 70 лет");
    }

    @Test
    void formatTelegramMessage_shouldFormatDeathEvents() {
        // given
        setupMessageHelperForDeathEvents();
        GenealogyResponse response = createResponseWithDeathEvents();

        // when
        String result = service.formatTelegramMessage(response);

        // then
        assertThat(result).contains("Прошедшие:");
        assertThat(result).contains("день памяти");
        assertThat(result).contains("Сидоров Сидор Сидорович");
        assertThat(result).contains("1 год назад");
        assertThat(result).contains("в возрасте");
        assertThat(result).contains("82 года");
    }

    @Test
    void formatEmailMessage_shouldFormatAllEventTypes() {
        // given
        setupMessageHelperForAllEvents();
        GenealogyResponse response = createResponseWithAllEvents();

        // when
        String result = service.formatEmailMessage(response);

        // then
        assertThat(result).contains("Прошедшие");
        assertThat(result).contains("Сегодня");
        assertThat(result).contains("Скоро");
        assertThat(result).contains("день рождения");
        assertThat(result).contains("день памяти");
    }

    @Test
    void formatEmailMessage_shouldHandleNullEvents() {
        // given
        setupMessageHelperForTitleAndNoEvents();
        GenealogyResponse response = new GenealogyResponse(
                null, null, null, null, null, null, null, null, null, false
        );

        // when
        String result = service.formatEmailMessage(response);

        // then
        assertThat(result).contains("📅 События из родословной");
        assertThat(result).doesNotContain("Прошедшие");
        assertThat(result).doesNotContain("Сегодня");
        assertThat(result).doesNotContain("Скоро");
    }

    @Test
    void formatEmailMessage_shouldHandleEmptyLists() {
        // given
        setupMessageHelperForTitleAndNoEvents();
        GenealogyResponse response = new GenealogyResponse(
                List.of(), List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), List.of(), false
        );

        // when
        String result = service.formatEmailMessage(response);

        // then
        assertThat(result).contains("📅 События из родословной");
        assertThat(result).doesNotContain("Прошедшие");
    }

    private void setupMessageHelperForTitleAndNoEvents() {
        lenient().when(messageHelper.get("genealogy.title")).thenReturn("📅 События из родословной");
        lenient().when(messageHelper.get("genealogy.no.events")).thenReturn("Нет событий для отображения");
    }

    private void setupMessageHelperForBirthEvents() {
        lenient().when(messageHelper.get("genealogy.title")).thenReturn("📅 События из родословной");
        lenient().when(messageHelper.get("genealogy.past")).thenReturn("Прошедшие");
        lenient().when(messageHelper.get("genealogy.birthday")).thenReturn("день рождения");
        lenient().when(messageHelper.get("genealogy.age.turned")).thenReturn("исполнилось %s");
        lenient().when(messageHelper.get("genealogy.year.one")).thenReturn("%d год");
        lenient().when(messageHelper.get("genealogy.year.few")).thenReturn("%d года");
        lenient().when(messageHelper.get("genealogy.year.many")).thenReturn("%d лет");
    }

    private void setupMessageHelperForBirthWouldEvents() {
        lenient().when(messageHelper.get("genealogy.title")).thenReturn("📅 События из родословной");
        lenient().when(messageHelper.get("genealogy.past")).thenReturn("Прошедшие");
        lenient().when(messageHelper.get("genealogy.birthday")).thenReturn("день рождения");
        lenient().when(messageHelper.get("genealogy.age.would")).thenReturn("%d лет назад");
        lenient().when(messageHelper.get("genealogy.year.one")).thenReturn("%d год");
        lenient().when(messageHelper.get("genealogy.year.few")).thenReturn("%d года");
        lenient().when(messageHelper.get("genealogy.year.many")).thenReturn("%d лет");
    }

    private void setupMessageHelperForDeathEvents() {
        lenient().when(messageHelper.get("genealogy.title")).thenReturn("📅 События из родословной");
        lenient().when(messageHelper.get("genealogy.past")).thenReturn("Прошедшие");
        lenient().when(messageHelper.get("genealogy.memory")).thenReturn("день памяти");
        lenient().when(messageHelper.get("genealogy.ago")).thenReturn("назад");
        lenient().when(messageHelper.get("genealogy.age")).thenReturn("в возрасте");
        lenient().when(messageHelper.get("genealogy.year.one")).thenReturn("%d год");
        lenient().when(messageHelper.get("genealogy.year.few")).thenReturn("%d года");
        lenient().when(messageHelper.get("genealogy.year.many")).thenReturn("%d лет");
        lenient().when(messageHelper.get("genealogy.age.death")).thenReturn("%s");
    }

    private void setupMessageHelperForAllEvents() {
        lenient().when(messageHelper.get("genealogy.title")).thenReturn("📅 События из родословной");
        lenient().when(messageHelper.get("genealogy.past")).thenReturn("Прошедшие");
        lenient().when(messageHelper.get("genealogy.today")).thenReturn("Сегодня");
        lenient().when(messageHelper.get("genealogy.future")).thenReturn("Скоро");
        lenient().when(messageHelper.get("genealogy.birthday")).thenReturn("день рождения");
        lenient().when(messageHelper.get("genealogy.memory")).thenReturn("день памяти");
        lenient().when(messageHelper.get("genealogy.ago")).thenReturn("назад");
        lenient().when(messageHelper.get("genealogy.age")).thenReturn("в возрасте");
        lenient().when(messageHelper.get("genealogy.year.one")).thenReturn("%d год");
        lenient().when(messageHelper.get("genealogy.year.few")).thenReturn("%d года");
        lenient().when(messageHelper.get("genealogy.year.many")).thenReturn("%d лет");
        lenient().when(messageHelper.get("genealogy.age.would")).thenReturn("%d лет назад");
        lenient().when(messageHelper.get("genealogy.age.future")).thenReturn("исполнится %s");
        lenient().when(messageHelper.get("genealogy.age.turned")).thenReturn("исполнилось %s");
        lenient().when(messageHelper.get("genealogy.age.death")).thenReturn("%s");
    }

    private GenealogyResponse createResponseWithBirthEvents() {
        Person person = new Person(1, "Иванов", null, "Иван", "Иванович");
        Date date = new Date("1956", "08", "13");
        Age age = new Age(70, 0, 0);
        BirthEvent birth = new BirthEvent(date, person, age);

        return new GenealogyResponse(
                List.of(birth),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }

    private GenealogyResponse createResponseWithBirthWouldEvents() {
        Person person = new Person(2, "Петров", null, "Петр", "Петрович");
        Date date = new Date("1978", "08", "14");
        Age age = new Age(48, 0, 0);
        BirthWouldEvent birthWould = new BirthWouldEvent(date, person, age);

        return new GenealogyResponse(
                null,
                List.of(birthWould),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }

    private GenealogyResponse createResponseWithDeathEvents() {
        Person person = new Person(17, "Сидоров", null, "Сидор", "Сидорович");
        Date date = new Date("2025", "08", "16");
        Age age = new Age(82, 0, 0);
        Interval interval = new Interval(1, 0, 0);
        DeathEvent death = new DeathEvent(date, person, age, interval);

        return new GenealogyResponse(
                null,
                null,
                List.of(death),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }

    private GenealogyResponse createResponseWithAllEvents() {
        Person person1 = new Person(1, "Иванов", null, "Иван", "Иванович");
        Person person2 = new Person(2, "Петров", null, "Петр", "Петрович");
        Person person3 = new Person(3, "Сидоров", null, "Сидор", "Сидорович");

        Date date1 = new Date("1956", "08", "13");
        Date date2 = new Date("2025", "08", "16");

        Age age = new Age(70, 0, 0);
        Interval interval = new Interval(1, 0, 0);

        BirthEvent birth = new BirthEvent(date1, person1, age);
        BirthWouldEvent birthWould = new BirthWouldEvent(date1, person2, age);
        DeathEvent death = new DeathEvent(date2, person3, age, interval);

        return new GenealogyResponse(
                List.of(birth),
                List.of(birthWould),
                List.of(death),
                List.of(birth),
                List.of(birthWould),
                List.of(death),
                List.of(birth),
                List.of(birthWould),
                List.of(death),
                false
        );
    }
}