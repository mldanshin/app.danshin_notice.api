package net.danshin.danshin_notice_api.models.genealogy_dates.upcoming;

public record DeathEvent(
        Date date,
        Person person,
        Age age,
        Interval interval
) {
}
