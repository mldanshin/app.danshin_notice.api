package net.danshin.danshin_notice_api.models.channel;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
        @NotBlank(message = "{email.verification.token.required}")
        String token
) {
}
