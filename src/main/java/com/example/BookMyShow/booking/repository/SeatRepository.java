package com.example.BookMyShow.booking.repository;

import com.example.BookMyShow.booking.entity.Screen;
import com.example.BookMyShow.booking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreen(Screen screen);
}
