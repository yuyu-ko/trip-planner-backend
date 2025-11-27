package com.example.tripplanner.dto;
import lombok.Data;

@Data
public class AuthRequest {
    private String email;    // 改成 email
    private String password;
}