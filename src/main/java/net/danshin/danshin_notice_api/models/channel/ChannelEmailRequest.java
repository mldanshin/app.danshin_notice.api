package net.danshin.danshin_notice_api.models.channel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChannelEmailRequest(
        @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}")
        @Size(max = 255, message = "{email.size}")
        String email
) {
}
