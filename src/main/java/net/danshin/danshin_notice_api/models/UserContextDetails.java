package net.danshin.danshin_notice_api.models;

import java.util.List;

public record UserContextDetails(
        String uuid,
        String email,
        List<String> roles
) {
}
