package com.example.tripplanner.config;

public class UserContext {
    public static final ThreadLocal<String> currentUser = new ThreadLocal<>();
}
