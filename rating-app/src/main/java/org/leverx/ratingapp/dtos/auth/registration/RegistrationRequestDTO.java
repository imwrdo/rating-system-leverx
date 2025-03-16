package org.leverx.ratingapp.dtos.auth.registration;

import lombok.Builder;

/**
 * DTO for user registration requests.
 * This record encapsulates user details required for registration.
 */
@Builder
public record RegistrationRequestDTO(String firstName, String lastName, String password, String email) {
}
