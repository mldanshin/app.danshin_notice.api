package net.danshin.danshin_notice_api.services;

import jakarta.mail.internet.MimeMessage;
import net.danshin.danshin_notice_api.helpers.MessageHelper;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender emailSender;
    private final MessageHelper messageHelper;
    private final RestTemplate telegramRestTemplate;
    private final String urlSendMessage;
    private final int connectTimeout;
    private final int readTimeout;
    private final String proxyHost;
    private final int proxyPort;

    @Value("${spring.mail.username}")
    @SuppressWarnings("unused")
    private String fromEmail;

    @Autowired
    public NotificationService(
            JavaMailSender emailSender,
            MessageHelper messageHelper,
            @Value("${telegram.bot.url.send-message}") String urlSendMessage,
            @Value("${spring.resttemplate.connect-timeout}") int connectTimeout,
            @Value("${spring.resttemplate.read-timeout}") int readTimeout,
            @Value("${proxy.host:}") String proxyHost,
            @Value("${proxy.port:0}") int proxyPort
    ) {
        this.emailSender = emailSender;
        this.messageHelper = messageHelper;
        this.urlSendMessage = urlSendMessage;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.telegramRestTemplate = createTelegramRestTemplate();
    }

    public void sendEmail(String to, String subject, String message) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);
            emailSender.send(mimeMessage);
        } catch (Exception _) {

        }
    }

    public void sendVerificationEmailLink(String to, String confirmUrl) {
        String htmlContent =
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head><meta charset='UTF-8'></head>" +
                        "<body style='font-family: Arial, sans-serif;'>" +
                        "  <h2>" + messageHelper.get("email.verification.greeting") + "</h2>" +
                        "  <p>" + messageHelper.get("email.verification.body") + "</p>" +
                        "  <a href='" + confirmUrl + "' " +
                        "     style='display: inline-block; padding: 12px 24px; " +
                        "            background-color: #4CAF50; color: white; " +
                        "            text-decoration: none; border-radius: 4px;'>" +
                        "    " + messageHelper.get("email.verification.button-text") +
                        "  </a>" +
                        "  <p>" + messageHelper.get("email.verification.expiry") + "</p>" +
                        "  <p>" + messageHelper.get("email.verification.ignore") + "</p>" +
                        "  <br>" +
                        "  <p>" + messageHelper.get("email.verification.signature") + "<br>" + messageHelper.get("email.verification.team") + "</p>" +
                        "</body>" +
                        "</html>";
        sendEmail(to, messageHelper.get("email.verification.subject"), htmlContent);
    }

    public void sendTelegramMessage(String chatId, String text) {
        try {
            Map<String, Object> request = Map.of(
                    "chat_id", chatId,
                    "text", text,
                    "parse_mode", "HTML"
            );
            log.info("Send telegram message. UrlSendMessage: {}.", urlSendMessage);
            ResponseEntity<@NonNull String> response = telegramRestTemplate.postForEntity(
                    urlSendMessage,
                    request,
                    String.class
            );

            if (response.getBody() != null && response.getBody().contains("\"ok\":false")) {
                log.error("Telegram API error: {}", response.getBody());
            } else {
                log.info("Telegram message sent to chat {}", chatId);
            }
        } catch (Exception e) {
            log.error("Error sending Telegram message: {}", e.getMessage(), e);
        }
    }

    public void sendVerificationTelegramSuccess(String chatId) {
        String message =
                messageHelper.get("telegram.verification.success") + "\n\n" +
                        messageHelper.get("telegram.verification.body") + "\n\n" +
                        messageHelper.get("telegram.verification.hint");

        sendTelegramMessage(chatId, message);
    }

    private RestTemplate createTelegramRestTemplate() {
        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            try {
                HttpHost proxy = HttpHost.create(proxyHost + ":" + proxyPort);

                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(Timeout.ofSeconds(30))
                        .setResponseTimeout(Timeout.ofSeconds(30))
                        .build();

                CloseableHttpClient httpClient = HttpClients.custom()
                        .setProxy(proxy)
                        .setDefaultRequestConfig(requestConfig)
                        .build();

                HttpComponentsClientHttpRequestFactory factory =
                        new HttpComponentsClientHttpRequestFactory(httpClient);

                return new RestTemplate(factory);

            } catch (Exception e) {
                log.error("❌ Failed to create RestTemplate with proxy: {}", e.getMessage(), e);
            }
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }
}
