package net.danshin.danshin_notice_api.config;

import net.danshin.danshin_notice_api.filters.ApiKeyAuthenticationFilter;
import net.danshin.danshin_notice_api.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@SuppressWarnings("unused")
public class SecurityConfig {

    private final ApiKeyAuthenticationFilter apiKeyAuthFilter;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(ApiKeyAuthenticationFilter apiKeyAuthFilter, JwtAuthenticationFilter jwtAuthFilter) {
        this.apiKeyAuthFilter = apiKeyAuthFilter;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/index.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/v2/api-docs/**").permitAll()
                        .requestMatchers("/api/channel/webhook/telegram").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterAt(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
