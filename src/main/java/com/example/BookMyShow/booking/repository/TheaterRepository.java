package com.example.BookMyShow.booking.repository;

import com.example.BookMyShow.booking.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}
