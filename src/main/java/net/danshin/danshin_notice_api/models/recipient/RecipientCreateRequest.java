package net.danshin.danshin_notice_api.models.recipient;

import jakarta.validation.constraints.NotNull;

public record RecipientCreateRequest(
        @NotNull String uuid
) {
}
