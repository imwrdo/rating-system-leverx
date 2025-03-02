package org.leverx.ratingapp.dto;

import lombok.Getter;

@Getter
public record RegistrationRequestDTO(String first_name, String last_name, String password, String email) {
}
