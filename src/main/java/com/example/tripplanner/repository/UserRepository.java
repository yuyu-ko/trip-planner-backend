package com.example.tripplanner.repository;

import com.example.tripplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // Spring Data JPA 會自動實作這個方法
    Optional<User> findByEmail(String email);
}