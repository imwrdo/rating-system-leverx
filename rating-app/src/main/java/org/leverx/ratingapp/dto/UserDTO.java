package org.leverx.ratingapp.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.leverx.ratingapp.enums.Role;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;

    private String first_name;

    private String last_name;

    private Boolean is_activated;

    private String password;

    private String email;

    private LocalDateTime created_at;

    private Role role;
}
