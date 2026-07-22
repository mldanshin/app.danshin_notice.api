package net.danshin.danshin_notice_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@SuppressWarnings("unused")
public class RestClientConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean("downloadRestClient")
    public RestClient downloadRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofMinutes(2));

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
