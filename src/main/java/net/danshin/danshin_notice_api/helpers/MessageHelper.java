package net.danshin.danshin_notice_api.helpers;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageHelper {

    private final MessageSource messageSource;

    public MessageHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String key, Object... args) {
        return messageSource.getMessage(
                key,
                args,
                LocaleContextHolder.getLocale()
        );
    }
}
