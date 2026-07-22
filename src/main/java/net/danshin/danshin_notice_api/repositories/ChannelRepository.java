package net.danshin.danshin_notice_api.repositories;

import net.danshin.danshin_notice_api.models.channel.ChannelEmailResponse;
import net.danshin.danshin_notice_api.models.channel.ChannelTelegramResponse;
import net.danshin.danshin_notice_api.models.channel.ChannelsResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository {
    ChannelsResponse getChannels(String recipientUuid);

    ChannelEmailResponse getEmail(String recipientUuid);

    ChannelTelegramResponse getTelegram(String recipientUuid);

    void createTelegram(String recipientUuid, String chatId, String username);

    void saveVerificationToken(String recipientUuid, String email, String token, int expiresInMinutes);

    void verifyEmailByToken(String recipientUuid, String token);

    void deleteEmail(String recipientUuid, String email);

    void deleteTelegram(String recipientUuid, String chatId);
}
