package com.example.tripplanner.repository;

import com.example.tripplanner.model.Trip;
import com.example.tripplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, String> {

    // 1. 回傳 List<Trip> (因為一個使用者有多個行程)
    // 2. 方法名稱建議用 findAllByUserId (語意更精確)
    List<Trip> findAllByUserId(String userId);
}
