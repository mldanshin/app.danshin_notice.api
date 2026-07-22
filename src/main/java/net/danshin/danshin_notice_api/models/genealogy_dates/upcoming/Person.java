package net.danshin.danshin_notice_api.models.genealogy_dates.upcoming;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record Person(
        int id,
        @Nullable String surname,
        @Nullable List<String> oldSurname,
        @Nullable String name,
        @Nullable String patronymic
) {
}
