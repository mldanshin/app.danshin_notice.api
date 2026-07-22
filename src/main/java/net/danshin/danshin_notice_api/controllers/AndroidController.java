package net.danshin.danshin_notice_api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import net.danshin.danshin_notice_api.services.AndroidService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/android")
@SuppressWarnings("unused")
public class AndroidController {
    private final AndroidService service;

    public AndroidController(AndroidService service) {
        this.service = service;
    }

    @GetMapping("/app-phone/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public ResponseEntity<byte[]> download() {
        byte[] data = service.downloadFile();

        if (data == null || data.length == 0) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"app.apk\"")
                .contentLength(data.length)
                .body(data);
    }

    @GetMapping("/app-phone/link-ru-store")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELATIVE', 'TRUSTED')")
    @Operation(summary = "Roles: ADMIN, RELATIVE, TRUSTED")
    public String linkRuStore() {
        return service.linkRuStore();
    }
}
