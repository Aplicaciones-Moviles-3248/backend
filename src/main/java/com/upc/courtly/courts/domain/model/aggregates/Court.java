package com.upc.courtly.courts.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String type;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    public Court(String name, String location, String type, String imageUrl, BigDecimal pricePerHour) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.imageUrl = imageUrl;
        this.pricePerHour = pricePerHour;
    }

    public void updateCourt(String name, String location, String type, String imageUrl, BigDecimal pricePerHour) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.imageUrl = imageUrl;
        this.pricePerHour = pricePerHour;
    }
}
