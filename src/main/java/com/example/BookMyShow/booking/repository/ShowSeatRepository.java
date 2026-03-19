package com.example.BookMyShow.booking.repository;

import com.example.BookMyShow.booking.entity.ShowSeat;
import com.example.BookMyShow.booking.entity.ShowSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    @Query("""
           SELECT ss
           FROM ShowSeat ss
           WHERE ss.show.id = :showId
             AND ss.status = :status
             AND (ss.expiresAt IS NULL OR ss.expiresAt > :now)
           """)
    List<ShowSeat> findAvailableSeatsByShowId(@Param("showId") Long showId,
                                              @Param("status") ShowSeatStatus status,
                                              @Param("now") Instant now);
}

