package net.danshin.danshin_notice_api.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class AndroidService {
    private final RestClient restClient;
    private final RestClient downloadRestClient;

    @Value("${yandex_api_token}")
    @SuppressWarnings("unused")
    private String yandexApiToken;

    @Value("${yandex_api_url_app_android_phone}")
    @SuppressWarnings("unused")
    private String yandexApiUrlAppAndroidPhone;

    public AndroidService(
            RestClient restClient,
            @Qualifier("downloadRestClient") RestClient downloadRestClient
    ) {
        this.restClient = restClient;
        this.downloadRestClient = downloadRestClient;
    }

    public byte[] downloadFile() {
        DownloadLinkResponse link = getDownloadLink();

        return downloadRestClient.get()
                .uri(URI.create(link.href()))
                .retrieve()
                .body(byte[].class);
    }

    public String linkRuStore() {
        return "";
    }

    private DownloadLinkResponse getDownloadLink() {
        URI uri = UriComponentsBuilder
                .fromUriString(yandexApiUrlAppAndroidPhone)
                .build(true)
                .toUri();
        return restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "OAuth " + yandexApiToken)
                .retrieve()
                .body(DownloadLinkResponse.class);
    }

    private record DownloadLinkResponse(
            String href,
            String method,
            boolean templated
    ) {
    }
}
