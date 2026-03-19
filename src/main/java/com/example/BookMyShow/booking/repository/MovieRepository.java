package com.example.BookMyShow.booking.repository;

import com.example.BookMyShow.booking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
