package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.models.channel.ChannelEmailResponse;
import net.danshin.danshin_notice_api.models.channel.ChannelTelegramResponse;
import net.danshin.danshin_notice_api.models.channel.ChannelsResponse;
import net.danshin.danshin_notice_api.repositories.ChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Service
public class ChannelService {
    private static final Logger log = LoggerFactory.getLogger(ChannelService.class);
    private final ChannelRepository repository;
    private final NotificationService notificationService;
    private final String confirmUrl;
    @Value("${verify.email.token.expires-minutes}")
    @SuppressWarnings("unused")
    private int verifyEmailTokenExpiresMinutes;

    public ChannelService(
            ChannelRepository repository,
            NotificationService notificationService,
            @Value("${verify.email.confirm-url}") String confirmUrl
    ) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.confirmUrl = confirmUrl;
    }

    public ChannelsResponse getChannels(String recipientUuid) {
        return repository.getChannels(recipientUuid);
    }

    public ChannelEmailResponse getEmail(String recipientUuid) {
        return repository.getEmail(recipientUuid);
    }

    public ChannelTelegramResponse getTelegram(String recipientUuid) {
        return repository.getTelegram(recipientUuid);
    }

    public void requestEmailVerification(String recipientUuid, String email) {
        String token = UUID.randomUUID().toString();

        repository.saveVerificationToken(recipientUuid, email, token, verifyEmailTokenExpiresMinutes);

        notificationService.sendVerificationEmailLink(email, confirmUrl + token);
    }

    public void verifyEmailByToken(String recipientUuid, String token) {
        repository.verifyEmailByToken(recipientUuid, token);
    }

    public void handleTelegramUpdate(Map<String, Object> update) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TelegramUpdate telegramUpdate = mapper.convertValue(update, TelegramUpdate.class);

            String chatId = telegramUpdate.message().chat().id().toString();
            log.info("chatId: {}", chatId);
            String text = telegramUpdate.message().text();
            String username = telegramUpdate.message().chat().username();
            log.info("username: {}", username);

            if (text != null && text.startsWith("/start")) {
                String[] parts = text.split(" ");
                if (parts.length == 2) {
                    String uuid = parts[1];
                    log.info("uuid: {}", uuid);
                    repository.createTelegram(uuid, chatId, username);
                    log.info("channel create success");
                    notificationService.sendVerificationTelegramSuccess(chatId);
                }
            } else {
                log.info("text == null or not starts with");
            }
        } catch (Exception _) {
            log.info("channel create error");
        }
    }

    public void deleteEmail(String recipientUuid, String email) {
        repository.deleteEmail(recipientUuid, email);
    }

    public void deleteTelegram(String recipientUuid, String chatId) {
        repository.deleteTelegram(recipientUuid, chatId);
    }

    private record TelegramUpdate(TelegramMessage message) {
    }

    private record TelegramMessage(TelegramChat chat, String text) {
    }

    private record TelegramChat(Long id, String username) {
    }
}
