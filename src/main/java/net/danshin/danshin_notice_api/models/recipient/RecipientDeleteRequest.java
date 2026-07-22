package net.danshin.danshin_notice_api.models.recipient;

import jakarta.validation.constraints.NotNull;

public record RecipientDeleteRequest(
        @NotNull String uuid
) {
}
