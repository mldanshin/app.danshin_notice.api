package net.danshin.danshin_notice_api.models.genealogy_dates;

import jakarta.validation.constraints.NotNull;

public record PersonDateDeletedRequest(
        @NotNull
        int personId
) {
}
