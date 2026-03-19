package com.example.BookMyShow.auth.devseed;

import com.example.BookMyShow.auth.entity.AdminProfile;
import com.example.BookMyShow.auth.entity.AdminZone;
import com.example.BookMyShow.auth.entity.LoginUserCredRoleCheck;
import com.example.BookMyShow.auth.entity.Role;
import com.example.BookMyShow.auth.repository.AdminRepository;
import com.example.BookMyShow.auth.repository.AdminZoneRepository;
import com.example.BookMyShow.auth.repository.LoginCredRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final AdminZoneRepository adminZoneRepository;
    private final LoginCredRepository loginCredRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedZones();

        if (adminRepository.count() > 0) {
            return; // Data already seeded, skip
        }
        // Create root admin for WORLD
        AdminProfile rootAdmin = createAdmin("root@bms.com", "Root Admin", "rootpass", "WORLD");
        saveLogin(rootAdmin.getEmail(), Role.ROLE_ADMIN, rootAdmin.getId());

        // Regional Admins for major US states
        createAndMapAdmin("ca-admin@bms.com", "CA Admin", "capass", "US-CA");
        createAndMapAdmin("ny-admin@bms.com", "NY Admin", "nypass", "US-NY");
        createAndMapAdmin("tx-admin@bms.com", "TX Admin", "txpass", "US-TX");
        createAndMapAdmin("fl-admin@bms.com", "FL Admin", "flpass", "US-FL");

        // City-Level Admins
        createAndMapAdmin("nyc-admin@bms.com", "NYC Admin", "nycpass", "US-NY-NYC");
        createAndMapAdmin("la-admin@bms.com", "LA Admin", "lapass", "US-CA-LA");
        createAndMapAdmin("sea-admin@bms.com", "Seattle Admin", "seapass", "US-WA-SEA");

        System.out.println("✅ Admin profiles seeded in dev profile");
    }

    private void seedZones() {
        if (adminZoneRepository.count() > 0) {
            return; // Zones already exist, skip zone seeding
        }

        AdminZone world = saveZone("WORLD", "World", 0, null);

        AdminZone us = saveZone("US", "United States", 1, world);

        AdminZone ca = saveZone("US-CA", "California", 2, us);
        AdminZone ny = saveZone("US-NY", "New York", 2, us);
        AdminZone tx = saveZone("US-TX", "Texas", 2, us);
        AdminZone fl = saveZone("US-FL", "Florida", 2, us);
        AdminZone wa = saveZone("US-WA", "Washington", 2, us);

        saveZone("US-NY-NYC", "New York City", 3, ny);
        saveZone("US-CA-LA", "Los Angeles", 3, ca);
        saveZone("US-WA-SEA", "Seattle", 3, wa);

        System.out.println("✅ Admin zones seeded");
    }

    private AdminZone saveZone(String zoneCode, String zoneName, Integer level, AdminZone parentZone) {
        AdminZone zone = AdminZone.builder()
                .zoneCode(zoneCode)
                .zoneName(zoneName)
                .level(level)
                .parentZone(parentZone)
                .build();
        return adminZoneRepository.save(zone);
    }

    // createAdmin method to create a root admin profile
    private AdminProfile createAdmin(String email , String name , String password , String zoneCode) {
        AdminZone zone = adminZoneRepository.findByZoneCode(zoneCode)
                .orElseThrow(() -> new RuntimeException("Zone not found: " + zoneCode));

        AdminProfile admin = AdminProfile.builder()
                .email(email)
                .adminName(name)
                .password(passwordEncoder.encode(password))
                .adminZone(zone)
                .phoneNumber(generateFakePhone(email)) // Assuming this method generates a fake phone number
                .role(Role.ROLE_ADMIN) // Assuming Role is an enum with ROLE_ADMIN
                .build();
        return adminRepository.save(admin);
    }
    private void saveLogin(String username,Role role,Long entityId){
        LoginUserCredRoleCheck login = LoginUserCredRoleCheck.builder()
                .username(username)
                .role(role)
                .entity_id(entityId)
                .build();

        loginCredRepository.save(login);
    }
    private String generateAdminIdNo(String zoneCode) {
        return "ADM-" + zoneCode + "-" + System.currentTimeMillis();
    }
    private void createAndMapAdmin(String email , String name , String password , String zoneCode) {
        AdminProfile admin = createAdmin(email, name, password, zoneCode);
        saveLogin(email , Role.ROLE_ADMIN, admin.getId());
    }
    private String generateFakePhone(String seed) {
        // You can base it on hash or email to keep unique values
        int hash = Math.abs(seed.hashCode());
        return "999-" + (hash % 1000) + "-" + (hash % 10000);  // e.g., 999-432-8756
    }
}
