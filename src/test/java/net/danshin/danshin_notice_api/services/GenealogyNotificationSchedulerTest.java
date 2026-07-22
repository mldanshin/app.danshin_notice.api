package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.PersonDate;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.Recipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenealogyNotificationSchedulerTest {

    @Mock
    private GenealogyDatesUpcomingService genealogyDatesUpcomingService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private GenealogyNotificationScheduler scheduler;

    @Test
    void initDailyRecipients_shouldInitializeRecipients() {
        // given
        List<Recipient> mockRecipients = createMockRecipients();
        when(genealogyDatesUpcomingService.getRecipients()).thenReturn(mockRecipients);

        // when
        scheduler.initDailyRecipients();

        // then
        verify(genealogyDatesUpcomingService).getRecipients();
    }

    @Test
    void initDailyRecipients_shouldNotScheduleWhenNoRecipients() {
        // given
        when(genealogyDatesUpcomingService.getRecipients()).thenReturn(List.of());

        // when
        scheduler.initDailyRecipients();

        // then
        verify(genealogyDatesUpcomingService).getRecipients();
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(notificationService, never()).sendTelegramMessage(anyString(), anyString());
    }

    @Test
    void initDailyRecipients_shouldNotScheduleWhenNoPersons() {
        // given
        List<Recipient> recipients = List.of(
                new Recipient(
                        1,
                        LocalTime.of(12, 0),
                        "UTC",
                        1,
                        1,
                        null,
                        "test@example.com",
                        "tg123"
                )
        );

        when(genealogyDatesUpcomingService.getRecipients()).thenReturn(recipients);

        // when
        scheduler.initDailyRecipients();

        // then
        verify(genealogyDatesUpcomingService).getRecipients();
        // Задача запланирована, но не выполнена, поэтому отправки нет
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(notificationService, never()).sendTelegramMessage(anyString(), anyString());
    }

    private List<Recipient> createMockRecipients() {
        return List.of(
                new Recipient(
                        1,
                        LocalTime.of(12, 30),
                        "Europe/Moscow",
                        12,
                        10,
                        List.of(new PersonDate(1, DateType.BIRTH)),
                        null,
                        null
                ),
                new Recipient(
                        2,
                        LocalTime.of(9, 34),
                        "Asia/Dubai",
                        1,
                        1,
                        List.of(new PersonDate(2, DateType.BIRTH)),
                        "admin@example.com",
                        "tgadmin"
                )
        );
    }
}