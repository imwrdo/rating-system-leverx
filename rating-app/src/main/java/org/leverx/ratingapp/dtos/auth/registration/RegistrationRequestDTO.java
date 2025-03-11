package org.leverx.ratingapp.dtos.auth.registration;


public record RegistrationRequestDTO(String firstName, String lastName, String password, String email) {
}
