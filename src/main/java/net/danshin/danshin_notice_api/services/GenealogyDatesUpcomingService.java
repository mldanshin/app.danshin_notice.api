package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenealogyDatesUpcomingService {
    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    @Value("${genealogy-api-token}")
    @SuppressWarnings("unused")
    private String token;
    @Value("${genealogy-api-url}")
    @SuppressWarnings("unused")
    private String url;

    public GenealogyDatesUpcomingService(RestTemplate restTemplate, JdbcTemplate jdbcTemplate) {
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Recipient> getRecipients() {
        return initRecipients();
    }

    private List<Recipient> initRecipients() {
        String sql = """
                SELECT
                    r.id as recipient_id,
                    gd.id as gd_id,
                    gd.send_time,
                    gd.timezone,
                    gd.days_before,
                    gd.days_after,
                    MAX(ced.email) as email,
                    MAX(ctd.chat_id) as telegram_chat_id
                FROM genealogy_dates gd
                JOIN recipients r ON gd.recipient_id = r.id
                JOIN genealogy_dates_channels gdc ON gd.id = gdc.genealogy_date_id
                JOIN channels c ON gdc.channel_id = c.id
                LEFT JOIN channels_email_details ced ON c.id = ced.channel_id
                LEFT JOIN channels_telegram_details ctd ON c.id = ctd.channel_id
                WHERE gd.id IN (SELECT DISTINCT genealogy_date_id FROM genealogy_dates_persons)
                GROUP BY r.id
                """;

        List<RecipientTemp> tempRecipients = jdbcTemplate.query(
                sql,
                (rs, _) -> new RecipientTemp(
                        rs.getInt("recipient_id"),
                        rs.getInt("gd_id"),
                        rs.getTime("send_time") != null ? rs.getTime("send_time").toLocalTime() : LocalTime.of(9, 0),
                        rs.getString("timezone") != null ? rs.getString("timezone") : "UTC",
                        rs.getInt("days_before"),
                        rs.getInt("days_after"),
                        rs.getString("email"),
                        rs.getString("telegram_chat_id")
                )
        );

        List<Recipient> recipients = new ArrayList<>();
        for (RecipientTemp temp : tempRecipients) {
            List<PersonDate> personDates = jdbcTemplate.query(
                    "SELECT person_id, type_id FROM genealogy_dates_persons WHERE genealogy_date_id = ?",
                    ps -> ps.setInt(1, temp.gdId()),
                    (rs, _) -> new PersonDate(
                            rs.getInt("person_id"),
                            DateType.fromValue(rs.getInt("type_id"))
                    )
            );

            recipients.add(new Recipient(
                    temp.id(),
                    temp.sendTime(),
                    temp.timezone(),
                    temp.daysBefore(),
                    temp.daysAfter(),
                    personDates,
                    temp.email(),
                    temp.telegramChatId()
            ));
        }

        return recipients;
    }

    public GenealogyResponse getGenealogyData(LocalDate date, Recipient recipient) {
        GenealogyResponse response = fetchGenealogyData(date, recipient.daysBefore(), recipient.daysAfter());

        if (response == null || recipient.personDate() == null) {
            return null;
        }

        List<BirthEvent> filteredBeforeBirth = filterEvents(response.beforeBirth(), recipient.personDate(), DateType.BIRTH);
        List<BirthWouldEvent> filteredBeforeBirthWould = filterEvents(response.beforeBirthWould(), recipient.personDate(), DateType.BIRTH);
        List<DeathEvent> filteredBeforeDeath = filterEvents(response.beforeDeath(), recipient.personDate(), DateType.DEATH);
        List<BirthEvent> filteredTodayBirth = filterEvents(response.todayBirth(), recipient.personDate(), DateType.BIRTH);
        List<BirthWouldEvent> filteredTodayBirthWould = filterEvents(response.todayBirthWould(), recipient.personDate(), DateType.BIRTH);
        List<DeathEvent> filteredTodayDeath = filterEvents(response.todayDeath(), recipient.personDate(), DateType.DEATH);
        List<BirthEvent> filteredAfterBirth = filterEvents(response.afterBirth(), recipient.personDate(), DateType.BIRTH);
        List<BirthWouldEvent> filteredAfterBirthWould = filterEvents(response.afterBirthWould(), recipient.personDate(), DateType.BIRTH);
        List<DeathEvent> filteredAfterDeath = filterEvents(response.afterDeath(), recipient.personDate(), DateType.DEATH);

        boolean isEmpty = filteredBeforeBirth.isEmpty() &&
                filteredBeforeBirthWould.isEmpty() &&
                filteredBeforeDeath.isEmpty() &&
                filteredTodayBirth.isEmpty() &&
                filteredTodayBirthWould.isEmpty() &&
                filteredTodayDeath.isEmpty() &&
                filteredAfterBirth.isEmpty() &&
                filteredAfterBirthWould.isEmpty() &&
                filteredAfterDeath.isEmpty();

        return new GenealogyResponse(
                filteredBeforeBirth,
                filteredBeforeBirthWould,
                filteredBeforeDeath,
                filteredTodayBirth,
                filteredTodayBirthWould,
                filteredTodayDeath,
                filteredAfterBirth,
                filteredAfterBirthWould,
                filteredAfterDeath,
                isEmpty
        );
    }

    private GenealogyResponse fetchGenealogyData(LocalDate date, int daysBefore, int daysAfter) {
        try {
            String urlWithParams = String.format(
                    "%s?date=%s&before_day=%d&after_day=%d",
                    url, date.toString(), daysBefore, daysAfter
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Accept", "application/json");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<@NonNull GenealogyResponse> response = restTemplate.exchange(
                    urlWithParams,
                    HttpMethod.GET,
                    entity,
                    GenealogyResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private <T> List<T> filterEvents(List<T> events, List<PersonDate> personDates, DateType dateType) {
        if (events == null || events.isEmpty() || personDates == null || personDates.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> personIds = personDates.stream()
                .filter(pd -> pd.dateType() == dateType)
                .map(PersonDate::id)
                .toList();

        if (personIds.isEmpty()) {
            return new ArrayList<>();
        }

        return events.stream()
                .filter(event -> {
                    Person person = null;
                    if (event instanceof BirthEvent birth) {
                        person = birth.person();
                    } else if (event instanceof BirthWouldEvent birthWould) {
                        person = birthWould.person();
                    } else if (event instanceof DeathEvent death) {
                        person = death.person();
                    }
                    return person != null && personIds.contains(person.id());
                })
                .collect(Collectors.toList());
    }

    private record RecipientTemp(
            int id,
            int gdId,
            LocalTime sendTime,
            String timezone,
            int daysBefore,
            int daysAfter,
            @Nullable String email,
            @Nullable String telegramChatId
    ) {
    }
}
