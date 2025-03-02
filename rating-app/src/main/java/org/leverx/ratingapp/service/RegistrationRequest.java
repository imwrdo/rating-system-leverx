package org.leverx.ratingapp.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
public record RegistrationRequest(String first_name, String last_name, String password, String email) {
}
