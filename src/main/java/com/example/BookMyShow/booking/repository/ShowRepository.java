package com.example.BookMyShow.booking.repository;

import com.example.BookMyShow.booking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
