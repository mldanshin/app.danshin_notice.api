package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.models.UserContextDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthService {
    public static String getUserUuid() {
        UserContextDetails details = getUserDetails();
        return details != null ? details.uuid() : null;
    }

    private static UserContextDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object details = authentication.getDetails();
        if (details instanceof UserContextDetails) {
            return (UserContextDetails) details;
        }
        return null;
    }
}
