package org.leverx.ratingapp.dtos.auth;

import lombok.Builder;

/**
 * DTO for user authentication requests.
 * This record encapsulates the login credentials needed for authentication.
 */
@Builder
public record AuthenticationRequestDTO(String email,String password) {
}
