package org.leverx.ratingapp.entity.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.leverx.ratingapp.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
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

    public ConfirmationToken(String token,
                             LocalDateTime createDateTime,
                             LocalDateTime expiryDateTime,
                             User user) {
        this.token = token;
        this.createDateTime = createDateTime;
        this.expiryDateTime = expiryDateTime;
        this.user = user;
    }
}
