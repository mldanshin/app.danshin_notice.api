package net.danshin.danshin_notice_api.models.genealogy_dates.upcoming;

import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;

public record PersonDate(
        int id,
        DateType dateType
) {
}
