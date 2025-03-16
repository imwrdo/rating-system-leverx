package org.leverx.ratingapp.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity representing a seller's rating. A SellerRating captures the rating of a seller by users,
 * including the current rating, the average rating, the number of comments, and the timestamp of the rating creation.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "seller_ratings")
public class SellerRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User user; // The seller to whom the rating belongs (foreign key to the User entity).

    @Column(nullable = false)
    private Integer rating; // The rating given to the seller (an integer value).

    @Column(nullable = false)
    private LocalDateTime createdAt; // Timestamp when the rating was created.

    @Column(name = "average_rating", nullable = false)
    private Double averageRating; // The average rating for the seller across all ratings.

    @Column(name = "total_comments", nullable = false)
    private Integer totalComments; // The total number of comments left for the seller.

    /**
     * This method is automatically called before the entity is persisted in the database.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // Set creation timestamp to the current time.
        rating = 0;  // Default rating is zero when the seller rating is created.
        averageRating = 0.0;  // Default average rating is zero when the seller rating is created.
        totalComments = 0;  // Default total comments is zero when the seller rating is created.
    }
}
