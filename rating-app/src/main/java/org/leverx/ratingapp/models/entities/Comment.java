package org.leverx.ratingapp.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity representing a comment made by a user about a seller. It contains details such as the message,
 * the author of the comment, the seller, the approval status, the grade, and the creation timestamp.
 */
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
    private String message; // The content of the comment, describing feedback about the seller.

    @ManyToOne
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id"
    )
    private User author; // The user who authored the comment.

    @ManyToOne
    @JoinColumn(
            name = "seller_id",
            referencedColumnName = "id",
            nullable = false
    )
    private User seller; // The seller who is being reviewed in the comment.

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt; // Timestamp of when the comment was created.

    @Column(name= "is_approved", nullable = false)
    @Builder.Default
    private Boolean isApproved = false; // Whether the comment is approved by admin or not.

    @Column(name = "grade", nullable = false)
    private Integer grade; // Rating/grade given by the author of the comment (e.g., 1-5 scale).

    /**
     * This method is automatically invoked before persisting the entity in the database.
     * It sets both the 'createdAt' field to the current timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
