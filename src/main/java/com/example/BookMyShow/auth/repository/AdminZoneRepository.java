package com.example.BookMyShow.auth.repository;

import com.example.BookMyShow.auth.entity.AdminZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminZoneRepository extends JpaRepository<AdminZone, Long> {
    // find an AdminZone by its zone code
    Optional<AdminZone> findByZoneCode(String zoneCode);
}
