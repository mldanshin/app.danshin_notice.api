package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.helpers.MessageHelper;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.GenealogyResponse;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.Recipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class GenealogyNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(GenealogyNotificationScheduler.class);

    private final GenealogyDatesUpcomingService genealogyDatesUpcomingService;
    private final NotificationService notificationService;
    private final MessageFormatterService messageFormatterService;
    private final MessageHelper messageHelper;

    private List<Recipient> recipients;
    private ScheduledExecutorService executorService;

    @Autowired
    public GenealogyNotificationScheduler(
            GenealogyDatesUpcomingService genealogyDatesUpcomingService,
            NotificationService notificationService,
            MessageFormatterService messageFormatterService, MessageHelper messageHelper) {
        this.genealogyDatesUpcomingService = genealogyDatesUpcomingService;
        this.notificationService = notificationService;
        this.messageFormatterService = messageFormatterService;
        this.messageHelper = messageHelper;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void initDailyRecipients() {
        log.info("Initialization of recipients for the day: {}", LocalDate.now());
        this.recipients = genealogyDatesUpcomingService.getRecipients();

        log.info("List all recipients: {}", recipients.stream()
                .map(Recipient::email)
                .collect(Collectors.joining(", ")));

        if (this.executorService != null && !this.executorService.isShutdown()) {
            this.executorService.shutdown();
            try {
                if (!this.executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    this.executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        this.executorService = Executors.newScheduledThreadPool(10);
        scheduleNotifications(LocalDate.now());
    }

    public void sendNowForRecipient(int recipientId, LocalDate date) {
        this.recipients = genealogyDatesUpcomingService.getRecipients();

        if (recipients == null || recipients.isEmpty()) {
            return;
        }

        Recipient target = recipients.stream()
                .filter(r -> r.id() == recipientId)
                .findFirst()
                .orElse(null);

        if (target == null) {
            return;
        }

        sendNotification(target, date);
    }

    private void scheduleNotifications(LocalDate date) {
        if (recipients == null || recipients.isEmpty()) {
            log.info("There are no recipients for this mailing");
            return;
        }

        for (Recipient recipient : recipients) {
            try {
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of(recipient.timezone()));
                ZonedDateTime sendTime = ZonedDateTime.of(
                        now.toLocalDate(),
                        recipient.sendTime(),
                        ZoneId.of(recipient.timezone())
                );

                long delay = java.time.Duration.between(now, sendTime).toMillis();
                if (delay < 0) {
                    sendTime = sendTime.plusDays(1);
                    delay = java.time.Duration.between(now, sendTime).toMillis();
                }

                log.info("Scheduling a shipment for a recipient {} in {}", recipient.email(), sendTime);

                executorService.schedule(
                        () -> sendNotification(recipient, date),
                        delay,
                        TimeUnit.MILLISECONDS
                );
            } catch (Exception e) {
                log.error("Error scheduling send to recipient: {}", e.getMessage());
            }
        }
    }

    private void sendNotification(Recipient recipient, LocalDate date) {
        try {
            if (recipient.personDate() == null || recipient.personDate().isEmpty()) {
                log.info("The recipient: {} has no persons", recipient.email());
                return;
            }

            GenealogyResponse response = genealogyDatesUpcomingService.getGenealogyData(
                    date,
                    recipient
            );

            if (response == null || response.isEmpty()) {
                log.info("There are no events for this date {}", date.toString());
                return;
            }

            sendToChannels(recipient, response);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
        }
    }

    private void sendToChannels(Recipient recipient, GenealogyResponse response) {
        String email = recipient.email();
        String telegramChatId = recipient.telegramChatId();

        if (email != null && !email.isEmpty()) {
            try {
                String emailMessage = messageFormatterService.formatEmailMessage(response);
                notificationService.sendEmail(
                        email,
                        messageHelper.get("genealogy.title"),
                        emailMessage
                );
                log.info("Email sent to {}", email);
            } catch (Exception e) {
                log.error("Error sending email to {}: {}", email, e.getMessage());
            }
        }

        if (telegramChatId != null && !telegramChatId.isEmpty()) {
            try {
                String telegramMessage = messageFormatterService.formatTelegramMessage(response);
                notificationService.sendTelegramMessage(
                        telegramChatId,
                        telegramMessage
                );
            } catch (Exception e) {
                log.error("Error sending Telegram to chat {}: {}", telegramChatId, e.getMessage());
            }
        }
    }
}
