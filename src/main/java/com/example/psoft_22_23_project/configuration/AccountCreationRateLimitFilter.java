package com.example.psoft_22_23_project.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountCreationRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, List<LocalDateTime>> ipRequestMap = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 3; // Maximum 3 account creations
    private static final Duration TIME_WINDOW = Duration.ofHours(24); // Within 24 hours

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/user/account") &&
                request.getMethod().equals("POST")) {

            String clientIp = getClientIP(request);

            if (isRateLimited(clientIp)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many account creation attempts from this IP address. Please try again later.");
                return;
            }

            recordRequest(clientIp);
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private synchronized boolean isRateLimited(String clientIp) {
        cleanupOldRequests(clientIp);

        List<LocalDateTime> requests = ipRequestMap.getOrDefault(clientIp, new ArrayList<>());
        return requests.size() >= MAX_REQUESTS;
    }

    private synchronized void recordRequest(String clientIp) {
        List<LocalDateTime> requests = ipRequestMap.getOrDefault(clientIp, new ArrayList<>());
        requests.add(LocalDateTime.now());
        ipRequestMap.put(clientIp, requests);
    }

    private void cleanupOldRequests(String clientIp) {
        List<LocalDateTime> requests = ipRequestMap.getOrDefault(clientIp, new ArrayList<>());

        if (!requests.isEmpty()) {
            LocalDateTime cutoffTime = LocalDateTime.now().minus(TIME_WINDOW);
            requests.removeIf(timestamp -> timestamp.isBefore(cutoffTime));

            if (requests.isEmpty()) {
                ipRequestMap.remove(clientIp);
            } else {
                ipRequestMap.put(clientIp, requests);
            }
        }
    }
}