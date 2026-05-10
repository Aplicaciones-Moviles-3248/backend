package com.upc.courtly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Seed logic moved to DataSeeder

@SpringBootApplication
@EnableJpaAuditing
public class CourtlyApplication {
    //Hello world
    public static void main(String[] args) {
        SpringApplication.run(CourtlyApplication.class, args);
    }

    // Data seeding is handled by `DataSeeder` component

}
