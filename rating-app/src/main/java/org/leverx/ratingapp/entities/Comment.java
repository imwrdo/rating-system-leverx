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
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="message", nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id"
    )
    private User author;

    @ManyToOne
    @JoinColumn(
            name = "seller_id",
            referencedColumnName = "id",
            nullable = false
    )
    private User seller;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name= "is_approved", nullable = false)
    private Boolean isApproved;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isApproved = false;
    }
}
