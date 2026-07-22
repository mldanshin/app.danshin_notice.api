package net.danshin.danshin_notice_api.models.genealogy_dates;

import java.time.LocalTime;
import java.util.List;

public record SettingsResponse(
        String uuid,
        LocalTime sendTime,
        String timezone,
        int dayBefore,
        int dayAfter,
        List<PersonDate> dates
) {
}
