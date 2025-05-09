package com.example.psoft_22_23_project.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountCreationRateLimitFilter extends OncePerRequestFilter {

  @Value("${account.creation.max-attempts}")
  private int MAX_REQUESTS;

    private static final Duration TIME_WINDOW = Duration.ofHours(24);

    private final ClientIPUtil clientIPUtil;

    private final Cache<String, List<LocalDateTime>> requestCache = Caffeine.newBuilder()
            .expireAfterWrite(TIME_WINDOW)
            .maximumSize(10_000)
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/user/account") && request.getMethod().equals("POST")) {
            String clientIp = clientIPUtil.getClientIP();

            if (isRateLimited(clientIp)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many account creation attempts from this IP address. Please try again later.");
                return;
            }

            recordRequest(clientIp);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String clientIp) {
        List<LocalDateTime> timestamps = requestCache.get(clientIp, k -> new ArrayList<>());
        LocalDateTime cutoff = LocalDateTime.now().minus(TIME_WINDOW);
        timestamps.removeIf(t -> t.isBefore(cutoff));

        return timestamps.size() >= MAX_REQUESTS;
    }

    private void recordRequest(String clientIp) {
        List<LocalDateTime> timestamps = requestCache.get(clientIp, k -> new ArrayList<>());
        timestamps.add(LocalDateTime.now());
        requestCache.put(clientIp, timestamps);
    }
}
