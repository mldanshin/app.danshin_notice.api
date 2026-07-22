package net.danshin.danshin_notice_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import net.danshin.danshin_notice_api.exceptoins.DataNotFoundException;
import net.danshin.danshin_notice_api.models.genealogy_dates.PersonDateCreatedRequest;
import net.danshin.danshin_notice_api.models.genealogy_dates.PersonDateDeletedRequest;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsRequest;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsResponse;
import net.danshin.danshin_notice_api.services.AuthService;
import net.danshin.danshin_notice_api.services.GenealogyDateService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genealogy_dates")
@SuppressWarnings("unused")
public class GenealogyDateController {
    private final GenealogyDateService genealogyDateService;

    public GenealogyDateController(GenealogyDateService genealogyDateService) {
        this.genealogyDateService = genealogyDateService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull SettingsResponse> get() throws DataNotFoundException {
        return ResponseEntity.ok(genealogyDateService.get(AuthService.getUserUuid()));
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull Void> create(
            @Valid @RequestBody SettingsRequest request
    ) {
        genealogyDateService.create(AuthService.getUserUuid(), request);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<@NonNull Void> delete() {
        genealogyDateService.delete(AuthService.getUserUuid());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events/person-created")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "roles: ADMIN")
    public ResponseEntity<@NonNull Void> handlePersonCreated(@RequestBody @NotNull PersonDateCreatedRequest request) {
        genealogyDateService.handleDateCreated(request.personId(), request.type());
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/events/person-deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "roles: ADMIN")
    public ResponseEntity<@NonNull Void> handlePersonDeleted(@RequestBody @NotNull PersonDateDeletedRequest request) {
        genealogyDateService.handlePersonDeleted(request.personId());
        return ResponseEntity.noContent().build();
    }
}
