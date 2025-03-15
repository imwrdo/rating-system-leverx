package org.leverx.ratingapp.dtos.auth.registration;

import lombok.Builder;
import lombok.NonNull;

@Builder
@NonNull
public record RegistrationRequestDTO(String firstName, String lastName, String password, String email) {
}
