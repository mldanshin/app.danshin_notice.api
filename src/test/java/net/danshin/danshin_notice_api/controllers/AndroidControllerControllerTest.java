package net.danshin.danshin_notice_api.controllers;

import net.danshin.danshin_notice_api.TestConstants;
import net.danshin.danshin_notice_api.services.AndroidService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AndroidControllerControllerTest {
    private final MockMvc mockMvc;

    @MockitoBean
    private final AndroidService androidService;

    @Autowired
    AndroidControllerControllerTest(MockMvc mockMvc, AndroidService androidService) {
        this.mockMvc = mockMvc;
        this.androidService = androidService;
    }

    @Test
    void download_returnsAttachment() throws Exception {
        byte[] content = "fake-apk".getBytes();

        when(androidService.downloadFile()).thenReturn(content);

        mockMvc.perform(get("/api/android/app-phone/download")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"app.apk\""))
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(content().bytes(content));
    }

    @Test
    void download_isAllowedForTrusted() throws Exception {
        when(androidService.downloadFile()).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/android/app-phone/download")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.TRUSTED_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void download_emptyBody_returnsServiceUnavailable() throws Exception {
        when(androidService.downloadFile()).thenReturn(new byte[0]);

        mockMvc.perform(get("/api/android/app-phone/download")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void download_forbiddenForOtherRoles() throws Exception {
        mockMvc.perform(get("/api/android/app-phone/download")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.USER_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    void download_unauthorizedWithoutUser() throws Exception {
        mockMvc.perform(get("/api/android/app-phone/download"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void linkRuStore_returnsString() throws Exception {
        when(androidService.linkRuStore()).thenReturn("https://www.rustore.ru/catalog/app/...");

        mockMvc.perform(get("/api/android/app-phone/link-ru-store")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestConstants.ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string("https://www.rustore.ru/catalog/app/..."));
    }
}
