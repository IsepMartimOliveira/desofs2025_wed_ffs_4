package com.example.psoft_22_23_project.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class ClientIPUtil {

    private static final List<String> HEADER_CANDIDATES = Arrays.asList(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    );

    /**
     * Get the client IP address from the request
     *
     * @return The client IP address or "unknown" if not found
     */
    public String getClientIP() {
        HttpServletRequest request;
        try {
            request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest();
        } catch (Exception e) {
            log.error("Could not get request attributes", e);
            return "unknown";
        }

        for (String header : HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr != null) ? remoteAddr : "unknown";
    }

    /**
     * Check if an IP value from a header is valid
     */
    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !ip.equalsIgnoreCase("unknown");
    }
}