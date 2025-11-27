package com.example.tripplanner.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<String, String> sessionStore = new ConcurrentHashMap<>();

    // 建立 session token
    public String createSession(String userId) {
        String token = UUID.randomUUID().toString();
        sessionStore.put(token, userId);
        return token;
    }

    // 根據 token 找 userId
    public String getUserId(String token) {
        return sessionStore.get(token);
    }
}
