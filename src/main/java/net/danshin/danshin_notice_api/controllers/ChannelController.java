package net.danshin.danshin_notice_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import net.danshin.danshin_notice_api.models.channel.*;
import net.danshin.danshin_notice_api.services.AuthService;
import net.danshin.danshin_notice_api.services.ChannelService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/channel")
@SuppressWarnings("unused")
public class ChannelController {
    private static final Logger log = LoggerFactory.getLogger(ChannelController.class);
    private final ChannelService service;
    @Value("${telegram.bot.url.start}")
    private String telegramBotStart;

    public ChannelController(ChannelService service) {
        this.service = service;
    }

    @GetMapping("/channels")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ChannelsResponse getChannels() {
        return service.getChannels(AuthService.getUserUuid());
    }

    @GetMapping("/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ChannelEmailResponse getEmail() {
        return service.getEmail(AuthService.getUserUuid());
    }

    @GetMapping("/telegram")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ChannelTelegramResponse getTelegram() {
        return service.getTelegram(AuthService.getUserUuid());
    }

    @PostMapping("/request-email-verification")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull Void> requestEmailVerification(@Valid @RequestBody ChannelEmailRequest request) {
        service.requestEmailVerification(AuthService.getUserUuid(), request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull Void> verifyEmailByLink(@Valid @RequestBody VerifyEmailRequest request) {
        service.verifyEmailByToken(AuthService.getUserUuid(), request.token());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/telegram/link")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull String> getTelegramLink() {
        String link = telegramBotStart + AuthService.getUserUuid();
        return ResponseEntity.ok(link);
    }

    @PostMapping("/webhook/telegram")
    public ResponseEntity<@NonNull Void> telegramWebhook(@RequestBody Map<String, Object> update) {
        log.info("Start telegram webhook (controller)");
        service.handleTelegramUpdate(update);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull String> deleteEmail(@RequestParam @NotBlank String email) {
        service.deleteEmail(AuthService.getUserUuid(), email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/telegram")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull String> deleteTelegram(@RequestParam @NotBlank String chatId) {
        service.deleteTelegram(AuthService.getUserUuid(), chatId);
        return ResponseEntity.noContent().build();
    }
}
