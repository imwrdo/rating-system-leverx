package org.leverx.ratingapp.entity.token;

import jakarta.persistence.*;
import lombok.*;
import org.leverx.ratingapp.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createDateTime;

    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    private LocalDateTime confirmationDateTime;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "app_user_id"
    )
    private User user;

}
