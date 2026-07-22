package net.danshin.danshin_notice_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import net.danshin.danshin_notice_api.models.recipient.RecipientDeleteRequest;
import net.danshin.danshin_notice_api.services.AuthService;
import net.danshin.danshin_notice_api.services.RecipientService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipients")
@SuppressWarnings("unused")
public class RecipientController {
    RecipientService service;

    @Autowired
    public RecipientController(RecipientService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull Void> create() {
        service.createRecipient(AuthService.getUserUuid());
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "roles: ADMIN")
    public ResponseEntity<@NonNull Void> delete(@RequestBody RecipientDeleteRequest request) {
        service.deleteRecipient(request.uuid());
        return ResponseEntity.noContent().build();
    }
}
