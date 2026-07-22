package net.danshin.danshin_notice_api.models.channel;

import org.jspecify.annotations.Nullable;

public record ChannelsResponse(
        @Nullable ChannelEmailResponse email,
        @Nullable ChannelTelegramResponse telegram
) {
}
