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
            referencedColumnName = "id",
            nullable = false
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
    private LocalDateTime created_at;

    @Column(name= "is_approved", nullable = false)
    private Boolean is_approved;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        is_approved = false;
    }
}
