package org.leverx.ratingapp.models.entities;


import jakarta.persistence.*;
import lombok.*;
import org.leverx.ratingapp.models.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Builder;

/**
 * The User entity represents a user of the system, either as an admin or a seller.
 * It implements the UserDetails interface from Spring Security to facilitate authentication and authorization.
 * This entity includes user information such as their first name, last name, email, password,
 * role (ADMIN or SELLER), account activation status, email confirmation status, and creation timestamp.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="first_name", nullable = false)
    private String firstName; // The first name of the user.

    @Column(name="last_name", nullable = false)
    private String lastName; // The last name of the user.

    @Column(name= "is_activated", nullable = false)
    @Builder.Default
    private Boolean isActivated=false; // Flag indicating whether the user's account is activated (by admin).

    @Column(name= "is_email_confirmed", nullable = false)
    @Builder.Default
    private Boolean isEmailConfirmed=false; // Flag indicating whether the user's email is confirmed (by user).

    @Column(name="password", nullable = false)
    private String password; // The password for the user's account.

    @Column(name="email", nullable = false, unique = true)
    private String email; // The unique email address of the user.

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // The timestamp when the user's account was created.

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false,updatable = false)
    private Role role; // The role of the user (e.g., ADMIN or SELLER).

    /**
     * This method is called before persisting the entity to the database.
     */
    @PrePersist
    protected void onCreate() {
        if(Role.ADMIN.equals(role)) {
            isActivated = true;  // Automatically activates the account for ADMIN role.
            isEmailConfirmed = true;  // Automatically confirms the email for ADMIN role.
        }
        createdAt = LocalDateTime.now();  // Set the creation timestamp to the current time.
    }

    /**
     * Returns a collection of authorities based on the user's role.
     * The role is converted into a SimpleGrantedAuthority object for Spring Security.
     *
     * @return Collection of {@link GrantedAuthority} for the user's role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (role==Role.ADMIN)
                ?List.of(new SimpleGrantedAuthority(Role.ADMIN.getValueOfRole()))
                :List.of(new SimpleGrantedAuthority(Role.SELLER.getValueOfRole()));
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
