package com.example.psoft_22_23_project.usermanagement.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Service for tracking and managing login attempts to prevent brute force attacks
 * Using Caffeine cache for automatic expiration
 */
@Service
@Slf4j
public class LoginAttemptService {
    @Value("${security.login.max-attempts}")
    private int maxUserAttempts;

    @Value("${security.login.max-attempts-ip}")
    private int maxIpAttempts;

    @Value("${security.login.lock-duration-seconds}")
    private int lockDurationSeconds;

    @Value("${security.login.ip-block-duration-seconds}")
    private int ipBlockDurationSeconds;

    private final Cache<String, Integer> userAttemptsCache;

    private final Cache<String, LocalDateTime> userBlockCache;

    private final Cache<String, Integer> ipAttemptsCache;

    private final Cache<String, LocalDateTime> ipBlockCache;

    public LoginAttemptService() {
        this.userAttemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();

        this.userBlockCache = Caffeine.newBuilder()
                .expireAfter(new BlockExpiryPolicy())
                .maximumSize(5_000)
                .build();

        this.ipAttemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(20_000)
                .build();

        this.ipBlockCache = Caffeine.newBuilder()
                .expireAfter(new BlockExpiryPolicy())
                .maximumSize(10_000)
                .build();
    }

    /**
     * Custom expiry policy that dynamically calculates expiration based on block time
     */
    private class BlockExpiryPolicy implements Expiry<String, LocalDateTime> {
        @Override
        public long expireAfterCreate(String key, LocalDateTime blockUntil, long currentTime) {
            return calculateRemainingNanos(blockUntil);
        }

        @Override
        public long expireAfterUpdate(String key, LocalDateTime blockUntil,
                                      long currentTime, @NonNegative long currentDuration) {
            return calculateRemainingNanos(blockUntil);
        }

        @Override
        public long expireAfterRead(String key, LocalDateTime blockUntil,
                                    long currentTime, @NonNegative long currentDuration) {
            return currentDuration;
        }

        private long calculateRemainingNanos(LocalDateTime unlockTime) {
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), unlockTime);
            return TimeUnit.SECONDS.toNanos(Math.max(0, seconds + 60));
        }
    }

    /**
     * Record a successful login attempt
     *
     * @param username The username of the user who successfully logged in
     * @param ip The IP address of the client
     */
    public void loginSucceeded(String username, String ip) {
        userAttemptsCache.invalidate(username);
        ipAttemptsCache.invalidate(ip);
        log.debug("Successful login: User={}, IP={}", username, ip);
    }

    /**
     * Record a failed login attempt and apply blocking rules
     *
     * @param username The username attempted
     * @param ip The IP address of the client
     */
    public void loginFailed(String username, String ip) {
        int userBackoff = calculateBackoffMultiplier(getUserAttemptsCount(username));
        int ipBackoff = calculateBackoffMultiplier(getIpAttemptsCount(ip));

        if (username != null && !username.isEmpty()) {
            int currentAttempts = userAttemptsCache.get(username, k -> 0) + 1;
            userAttemptsCache.put(username, currentAttempts);

            log.debug("Failed login attempt #{} for user: {}", currentAttempts, username);

            if (currentAttempts >= maxUserAttempts) {
                blockUser(username, userBackoff);
            }
        }

        int currentIpAttempts = ipAttemptsCache.get(ip, k -> 0) + 1;
        ipAttemptsCache.put(ip, currentIpAttempts);

        log.debug("Failed login attempt #{} from IP: {}", currentIpAttempts, ip);

        if (currentIpAttempts >= maxIpAttempts) {
            blockIp(ip, ipBackoff);
        }
    }

    /**
     * Calculate backoff multiplier based on number of attempts
     *
     * @param attempts Number of failed attempts
     * @return Backoff multiplier
     */
    private int calculateBackoffMultiplier(int attempts) {
        if (attempts <= maxUserAttempts) {
            return 1;
        } else {
            return (int) Math.pow(2, attempts - maxUserAttempts);
        }
    }

    /**
     * Block a user account for a duration
     *
     * @param username Username to block
     * @param backoffMultiplier Multiplier for lock duration
     */
    private void blockUser(String username, int backoffMultiplier) {
        int actualLockDuration = lockDurationSeconds * backoffMultiplier;
        LocalDateTime unlockTime = LocalDateTime.now().plusSeconds(actualLockDuration);
        userBlockCache.put(username, unlockTime);
        log.warn("User {} has been blocked until {}", username, unlockTime);
    }

    /**
     * Block an IP address for a duration
     *
     * @param ip IP address to block
     * @param backoffMultiplier Multiplier for block duration
     */
    private void blockIp(String ip, int backoffMultiplier) {
        int actualBlockDuration = ipBlockDurationSeconds * backoffMultiplier;
        LocalDateTime unlockTime = LocalDateTime.now().plusSeconds(actualBlockDuration);
        ipBlockCache.put(ip, unlockTime);
        log.warn("IP {} has been blocked until {}", ip, unlockTime);
    }

    /**
     * Check if a user is currently blocked
     *
     * @param username Username to check
     * @return true if user is blocked, false otherwise
     */
    public boolean isUserBlocked(String username) {
        LocalDateTime blockTime = userBlockCache.getIfPresent(username);
        if (blockTime != null) {
            if (LocalDateTime.now().isAfter(blockTime)) {
                userBlockCache.invalidate(username);
                userAttemptsCache.invalidate(username);
                log.debug("User {} block has expired", username);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Get the unlock time for a blocked user
     *
     * @param username Username to check
     * @return LocalDateTime when user will be unblocked, or null if not blocked
     */
    public LocalDateTime getUserUnlockTime(String username) {
        return userBlockCache.getIfPresent(username);
    }

    /**
     * Check if an IP address is currently blocked
     *
     * @param ip IP address to check
     * @return true if IP is blocked, false otherwise
     */
    public boolean isIpBlocked(String ip) {
        LocalDateTime blockTime = ipBlockCache.getIfPresent(ip);
        if (blockTime != null) {
            if (LocalDateTime.now().isAfter(blockTime)) {
                ipBlockCache.invalidate(ip);
                ipAttemptsCache.invalidate(ip);
                log.debug("IP {} block has expired", ip);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Get the unlock time for a blocked IP
     *
     * @param ip IP to check
     * @return LocalDateTime when IP will be unblocked, or null if not blocked
     */
    public LocalDateTime getIpUnlockTime(String ip) {
        return ipBlockCache.getIfPresent(ip);
    }

    /**
     * Get the current number of attempts for a username
     */
    private int getUserAttemptsCount(String username) {
        Integer attempts = userAttemptsCache.getIfPresent(username);
        return attempts != null ? attempts : 0;
    }

    /**
     * Get the current number of attempts for an IP
     */
    private int getIpAttemptsCount(String ip) {
        Integer attempts = ipAttemptsCache.getIfPresent(ip);
        return attempts != null ? attempts : 0;
    }

    /**
     * Get the number of login attempts remaining for a user before being blocked
     *
     * @param username Username to check
     * @return Number of attempts remaining
     */
    public int getUserAttemptsLeft(String username) {
        return maxUserAttempts - getUserAttemptsCount(username);
    }

    /**
     * Get the number of login attempts remaining for an IP before being blocked
     *
     * @param ip IP address to check
     * @return Number of attempts remaining
     */
    public int getIpAttemptsLeft(String ip) {
        return maxIpAttempts - getIpAttemptsCount(ip);
    }

    /**
     * Clean cache stats (no need for @Scheduled cleanup as Caffeine handles expiration)
     */
    public void logCacheStats() {
        log.info("Cache statistics - User blocks: {}, IP blocks: {}",
                userBlockCache.estimatedSize(), ipBlockCache.estimatedSize());
    }
}