package org.leverx.ratingapp.entities;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="game_objects")
public class GameObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            referencedColumnName = "id",
            name = "app_user_id"
    )
    private User user;

    @Column(name="created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updated_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = created_at;
    }
}
