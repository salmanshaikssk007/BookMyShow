package com.example.BookMyShow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "admin_zones") // Table name in the database
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminZone {

    @Id
    private String zoneCode; // "US" , "us-ca" ..etc

    @Column(nullable = false)
    private String zoneName; // Name of the zone, e.g., "United States", "California"

    @Column(nullable = false)
    private Integer level ; // 0 = root , 1 = country , 2 = state , 3 = zone .. etc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_zone_code")
    private AdminZone parentZone; // Parent zone, can be null if this is a root zone
}
