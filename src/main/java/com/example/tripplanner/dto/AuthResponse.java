package com.example.tripplanner.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String username;
    // ... 其他用戶資訊，例如 id
}