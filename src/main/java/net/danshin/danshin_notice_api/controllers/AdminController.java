package net.danshin.danshin_notice_api.controllers;

import net.danshin.danshin_notice_api.services.GenealogyNotificationScheduler;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@SuppressWarnings("unused")
public class AdminController {

    private final GenealogyNotificationScheduler scheduler;

    @Autowired
    public AdminController(GenealogyNotificationScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/send-now/{recipientId}")
    public ResponseEntity<@NonNull String> sendNowForRecipient(
            @PathVariable int recipientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        scheduler.sendNowForRecipient(recipientId, date);
        return ResponseEntity.ok("Notification sent to recipient " + recipientId + " for date: " + date);
    }
}