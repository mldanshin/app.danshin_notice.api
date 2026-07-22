package net.danshin.danshin_notice_api.repositories;

import net.danshin.danshin_notice_api.TestConstants;
import net.danshin.danshin_notice_api.exceptoins.DataNotFoundException;
import net.danshin.danshin_notice_api.models.channel.ChannelTelegramResponse;
import net.danshin.danshin_notice_api.repositories.impl.ChannelRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ChannelRepositoryTest {
    private static final String ADMIN_UUID = TestConstants.ADMIN_UUID;
    private final ChannelRepositoryImpl repository;

    @Autowired
    ChannelRepositoryTest(ChannelRepositoryImpl repository) {
        this.repository = repository;
    }

    // ==================== createTelegram TESTS ====================

    @Test
    void createTelegram_Success() {
        String newChatId = "newchatid";
        String newUsername = "newusername";

        repository.createTelegram(ADMIN_UUID, newChatId, newUsername);

        ChannelTelegramResponse response = repository.getTelegram(ADMIN_UUID);
        assertNotNull(response);
        assertEquals(newChatId, response.chatId());
        assertEquals(newUsername, response.username());
    }

    @Test
    void createTelegram_ReplacesOldTelegram() {
        String firstChatId = "firstchat";
        String secondChatId = "secondchat";

        repository.createTelegram(ADMIN_UUID, firstChatId, "user1");
        repository.createTelegram(ADMIN_UUID, secondChatId, "user2");

        ChannelTelegramResponse response = repository.getTelegram(ADMIN_UUID);
        assertEquals(secondChatId, response.chatId());
        assertEquals("user2", response.username());
    }

    @Test
    void createTelegram_ThrowsWhenRecipientNotFound() {
        String nonExistentUuid = "non-existent-uuid";

        assertThrows(DataNotFoundException.class,
                () -> repository.createTelegram(nonExistentUuid, "chatid", "username"));
    }
}
