package net.danshin.danshin_notice_api.models.genealogy_dates;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.danshin.danshin_notice_api.validation.ValidTimezone;

import java.time.LocalTime;
import java.util.List;

public record SettingsRequest(
        @NotNull LocalTime sendTime,
        @NotBlank @ValidTimezone String timezone,
        @NotNull @Min(value = 1) @Max(value = 30) int dayBefore,
        @NotNull @Min(value = 1) @Max(value = 30) int dayAfter,
        List<PersonDate> dates) {
}
