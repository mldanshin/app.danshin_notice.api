package net.danshin.danshin_notice_api.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.danshin.danshin_notice_api.models.UserContextDetails;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    @SuppressWarnings("unused")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain filterChain
    ) throws IOException, ServletException {
        if (response == null || filterChain == null || request == null) {
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.isEmpty() || !validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = parseToken(token);

            if (claims == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = claims.getSubject();
            String uuid = claims.get("uuid", String.class);

            Object rawRolesObj = claims.get("roles");
            List<String> roles;
            if (rawRolesObj instanceof List) {
                roles = ((List<?>) rawRolesObj).stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.toList());
            } else if (rawRolesObj instanceof String) {
                roles = Arrays.asList(((String) rawRolesObj).split("\\s*,\\s*"));
            } else {
                roles = Collections.emptyList();
            }

            if (email == null || uuid == null || roles.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid user data from Token\"}");
                filterChain.doFilter(request, response);
                return;
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            UserContextDetails userDetails = new UserContextDetails(uuid, email, roles);
            authToken.setDetails(userDetails);

            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (HttpClientErrorException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token verification failed\"}");
        } catch (Exception e) {
            logger.error("Error during JWT verification: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .clockSkewSeconds(60)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }
}
