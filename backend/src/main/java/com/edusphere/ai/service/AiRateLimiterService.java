package com.edusphere.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiRateLimiterService {

    private final int limitPerMinute;
    private final Map<String, UserRateState> userLimitMap = new ConcurrentHashMap<>();

    public AiRateLimiterService(@Value("${ai.rate-limit.requests-per-minute:20}") int limitPerMinute) {
        this.limitPerMinute = limitPerMinute;
    }

    public boolean isAllowed(String userEmail) {
        long now = System.currentTimeMillis();
        UserRateState state = userLimitMap.computeIfAbsent(userEmail, k -> new UserRateState(now, 0));

        synchronized (state) {
            if (now - state.windowStart > 60000) { // reset 1 min window
                state.windowStart = now;
                state.requestCount = 1;
                return true;
            } else {
                if (state.requestCount < limitPerMinute) {
                    state.requestCount++;
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    private static class UserRateState {
        long windowStart;
        int requestCount;

        UserRateState(long windowStart, int requestCount) {
            this.windowStart = windowStart;
            this.requestCount = requestCount;
        }
    }
}
