package com.example.tripplanner.service;

import com.example.tripplanner.model.User;
import com.example.tripplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean register(String email, String password) {
        if (repository.findByEmail(email).isPresent()) {
            return false; // email 已存在
        }

        User u = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .build();

        repository.save(u);
        return true;
    }

    public User login(String email, String password) {
        return repository.findByEmail(email)
                .filter(u -> encoder.matches(password, u.getPassword()))
                .orElse(null);
    }
}
