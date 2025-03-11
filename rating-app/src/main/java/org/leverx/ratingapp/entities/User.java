package org.leverx.ratingapp.entities;


import jakarta.persistence.*;
import lombok.*;
import org.leverx.ratingapp.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Builder;

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
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name= "is_activated", nullable = false)
    private Boolean isActivated;

    @Column(name="password", nullable = false)
    private String password;


    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false,updatable = false)
    private Role role;


    @PrePersist
    protected void onCreate() {
        isActivated = (role == Role.ADMIN)
                ?true
                :false;
        createdAt = LocalDateTime.now();
    }

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
