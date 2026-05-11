package com.upc.courtly.users.domain.model.aggregates;

import com.upc.courtly.iam.domain.model.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iam_user_id", unique = true)
    private User user;

    public UserProfile(String name, String email, String phone) {
        this(name, email, phone, null, null);
    }

    public UserProfile(String name, String email, String phone, User user) {
        this(name, email, phone, null, user);
    }

    public UserProfile(String name, String email, String phone, String imageUrl, User user) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public void updateProfile(String name, String email, String phone, String imageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
