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
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }


}
