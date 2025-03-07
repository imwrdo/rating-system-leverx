package org.leverx.ratingapp.dtos;

import org.leverx.ratingapp.enums.Role;
import java.time.LocalDateTime;


public record UserDTO(
        Long id,
        String first_name,
        String last_name,
        Boolean is_activated,
        String password,
        String email,
        LocalDateTime created_at,
        Role role) {


}
