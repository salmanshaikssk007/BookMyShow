package com.example.BookMyShow.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "show_seats",
    uniqueConstraints = @UniqueConstraint(columnNames = {"show_id", "seat_id"}),
    indexes = {
        @Index(name = "idx_show_seats_show_seat", columnList = "show_id, seat_id")
    }
)
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ShowSeatStatus status = ShowSeatStatus.AVAILABLE;

    @Version
    private Long version;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
