package org.leverx.ratingapp.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a game object. A game object is an entity that contains details about a game
 * such as its title, description (text), the user who created it, and timestamps for creation and updates.
 */
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
    private String title; // The title of the game object.

    @Column(name="text", nullable = false)
    private String text; // A textual description or content related to the game object.

    @ManyToOne
    @JoinColumn(
            nullable = false,
            referencedColumnName = "id",
            name = "app_user_id"
    )
    private User user; // The user who created the game object (foreign key to the User entity).

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt; // Timestamp when the game object was created.

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt; // Timestamp when the game object was last updated.

    /**
     * This method is automatically invoked before persisting the entity in the database.
     * It sets both the 'createdAt' and 'updatedAt' fields to the current timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }


}
