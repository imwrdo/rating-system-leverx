package org.leverx.ratingapp.dto.auth;


public record RegistrationRequestDTO(String first_name, String last_name, String password, String email) {
}
