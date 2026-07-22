package net.danshin.danshin_notice_api.models.genealogy_dates.upcoming;

import org.jspecify.annotations.Nullable;

import java.time.LocalTime;
import java.util.List;

public record Recipient(
        int id,
        LocalTime sendTime,
        String timezone,
        int daysBefore,
        int daysAfter,
        @Nullable List<PersonDate> personDate,
        @Nullable String email,
        @Nullable String telegramChatId
) {
}
