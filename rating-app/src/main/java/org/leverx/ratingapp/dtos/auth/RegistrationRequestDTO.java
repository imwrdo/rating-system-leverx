package org.leverx.ratingapp.dtos.auth;


public record RegistrationRequestDTO(String first_name, String last_name, String password, String email) {
}
