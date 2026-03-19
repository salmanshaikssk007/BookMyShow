package com.example.BookMyShow.booking.devseed;

import com.example.BookMyShow.booking.entity.*;
import com.example.BookMyShow.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Order(2)
public class BookingDataSeeder implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        // ── 2 Cities ──────────────────────────────────────────────────────────
        City mumbai = cityRepository.save(City.builder()
                .name("Mumbai").state("Maharashtra").country("India").build());
        City delhi = cityRepository.save(City.builder()
                .name("Delhi").state("Delhi").country("India").build());

        // ── 10 Theaters (5 per city) ──────────────────────────────────────────
        List<Theater> theaters = new ArrayList<>();

        // Mumbai
        theaters.add(save(Theater.builder().theaterName("PVR Juhu")
                .address("Juhu, Mumbai - 400049").city(mumbai).build()));
        theaters.add(save(Theater.builder().theaterName("INOX Nariman Point")
                .address("Nariman Point, Mumbai - 400021").city(mumbai).build()));
        theaters.add(save(Theater.builder().theaterName("Cinepolis Versova")
                .address("Versova, Andheri West, Mumbai - 400061").city(mumbai).build()));
        theaters.add(save(Theater.builder().theaterName("PVR Lower Parel")
                .address("Lower Parel, Mumbai - 400013").city(mumbai).build()));
        theaters.add(save(Theater.builder().theaterName("Miraj Cinemas Mulund")
                .address("Mulund West, Mumbai - 400080").city(mumbai).build()));

        // Delhi
        theaters.add(save(Theater.builder().theaterName("PVR Select City Walk")
                .address("Saket, New Delhi - 110017").city(delhi).build()));
        theaters.add(save(Theater.builder().theaterName("INOX Nehru Place")
                .address("Nehru Place, New Delhi - 110019").city(delhi).build()));
        theaters.add(save(Theater.builder().theaterName("Cinepolis DLF Avenue")
                .address("DLF Avenue, Saket, New Delhi - 110017").city(delhi).build()));
        theaters.add(save(Theater.builder().theaterName("PVR Connaught Place")
                .address("Connaught Place, New Delhi - 110001").city(delhi).build()));
        theaters.add(save(Theater.builder().theaterName("DT Cinemas Vasant Kunj")
                .address("Vasant Kunj, New Delhi - 110070").city(delhi).build()));

        // ── 5 Screens (1 per first 5 theaters) ───────────────────────────────
        List<Screen> screens = new ArrayList<>();
        screens.add(screenRepository.save(Screen.builder()
                .theater(theaters.get(0)).screenNo("S1").seatCapacity(20).screenType(ScreenType.IMAX).build()));
        screens.add(screenRepository.save(Screen.builder()
                .theater(theaters.get(1)).screenNo("S1").seatCapacity(20).screenType(ScreenType.STANDARD).build()));
        screens.add(screenRepository.save(Screen.builder()
                .theater(theaters.get(2)).screenNo("S1").seatCapacity(20).screenType(ScreenType.FOUR_DX).build()));
        screens.add(screenRepository.save(Screen.builder()
                .theater(theaters.get(3)).screenNo("S1").seatCapacity(20).screenType(ScreenType.STANDARD).build()));
        screens.add(screenRepository.save(Screen.builder()
                .theater(theaters.get(4)).screenNo("S1").seatCapacity(20).screenType(ScreenType.IMAX).build()));

        // ── 100 Seats (20 per screen: 4 rows × 5 seats) ──────────────────────
        // Row A, B → REGULAR | Row C → PREMIUM | Row D → RECLINER
        String[] rows = {"A", "B", "C", "D"};
        for (Screen screen : screens) {
            for (String row : rows) {
                SeatType type = switch (row) {
                    case "C" -> SeatType.PREMIUM;
                    case "D" -> SeatType.RECLINER;
                    default  -> SeatType.REGULAR;
                };
                for (int n = 1; n <= 5; n++) {
                    seatRepository.save(Seat.builder()
                            .screen(screen)
                            .rowNumber(row)
                            .seatNumber(String.valueOf(n))
                            .seatType(type)
                            .build());
                }
            }
        }

        // ── 2 Movies ──────────────────────────────────────────────────────────
        Movie inception = movieRepository.save(Movie.builder()
                .title("Inception")
                .duration(148)
                .language("English")
                .genre("Sci-Fi Thriller")
                .description("A thief who steals corporate secrets through dream-sharing technology " +
                        "is given the task of planting an idea into the mind of a C.E.O.")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .build());

        Movie rrr = movieRepository.save(Movie.builder()
                .title("RRR")
                .duration(187)
                .language("Hindi")
                .genre("Action Drama")
                .description("A fictitious story about two legendary revolutionaries and their journey " +
                        "before they started fighting for their country in the 1920s.")
                .releaseDate(LocalDate.of(2022, 3, 25))
                .build());

        // ── 3 Shows ───────────────────────────────────────────────────────────
        LocalDateTime base = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);

        Show show1 = showRepository.save(Show.builder()
                .movie(inception)
                .screen(screens.get(0))
                .startTime(base.withHour(10))
                .endTime(base.withHour(12).plusMinutes(28))
                .showStatus(ShowStatus.SCHEDULED)
                .build());

        Show show2 = showRepository.save(Show.builder()
                .movie(rrr)
                .screen(screens.get(1))
                .startTime(base.withHour(14))
                .endTime(base.withHour(17).plusMinutes(7))
                .showStatus(ShowStatus.SCHEDULED)
                .build());

        Show show3 = showRepository.save(Show.builder()
                .movie(inception)
                .screen(screens.get(2))
                .startTime(base.withHour(19))
                .endTime(base.withHour(21).plusMinutes(28))
                .showStatus(ShowStatus.SCHEDULED)
                .build());

        // ── ShowSeats (one per seat per show, all AVAILABLE) ──────────────────
        seedShowSeats(show1, screens.get(0));
        seedShowSeats(show2, screens.get(1));
        seedShowSeats(show3, screens.get(2));

        System.out.println("✅ Booking data seeded: 2 movies, 2 cities, 10 theaters, 5 screens, 100 seats, 3 shows");
    }

    private void seedShowSeats(Show show, Screen screen) {
        List<Seat> seats = seatRepository.findByScreen(screen);
        List<ShowSeat> showSeats = seats.stream()
                .map(seat -> ShowSeat.builder()
                        .show(show)
                        .seat(seat)
                        .status(ShowSeatStatus.AVAILABLE)
                        .build())
                .toList();
        showSeatRepository.saveAll(showSeats);
    }

    private Theater save(Theater theater) {
        return theaterRepository.save(theater);
    }
}
