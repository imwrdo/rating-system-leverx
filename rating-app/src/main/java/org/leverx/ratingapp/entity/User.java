package org.leverx.ratingapp.entity;


import jakarta.persistence.*;
import lombok.*;
import org.leverx.ratingapp.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="first_name", nullable = false)
    private String first_name;

    @Column(name="last_name", nullable = false)
    private String last_name;

    @Column(name= "is_activated", nullable = false)
    private Boolean is_activated;

    @Column(name="password", nullable = false)
    private String password;


    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false,updatable = false)
    private Role role;

    public User(String first_name, String last_name, String password, String email, Role role) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @PrePersist
    protected void onCreate() {
        is_activated = (role == Role.ADMIN)
                ?true
                :false;
        created_at = LocalDateTime.now();
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

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
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
