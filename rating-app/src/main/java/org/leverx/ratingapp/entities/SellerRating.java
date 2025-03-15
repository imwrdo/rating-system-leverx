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
@Table(name = "seller_ratings")
public class SellerRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating;

    @Column(name = "total_comments", nullable = false)
    private Integer totalComments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        rating = 0;
        averageRating = 0.0;
        totalComments = 0;
    }
}
