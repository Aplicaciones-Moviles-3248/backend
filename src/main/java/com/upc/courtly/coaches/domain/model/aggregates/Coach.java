package com.upc.courtly.coaches.domain.model.aggregates;

import com.upc.courtly.iam.domain.model.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coaches")
@Getter
@Setter
@NoArgsConstructor
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String expertise;

    @Column(nullable = false)
    private String phone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iam_user_id", unique = true)
    private User user;

    public Coach(String name, String expertise, String phone) {
        this(name, expertise, phone, null);
    }

    public Coach(String name, String expertise, String phone, User user) {
        this.name = name;
        this.expertise = expertise;
        this.phone = phone;
        this.user = user;
    }

    public void updateCoach(String name, String expertise, String phone) {
        this.name = name;
        this.expertise = expertise;
        this.phone = phone;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
