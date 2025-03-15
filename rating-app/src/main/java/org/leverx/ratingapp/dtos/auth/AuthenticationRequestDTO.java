package org.leverx.ratingapp.dtos.auth;

import lombok.Builder;

@Builder
public record AuthenticationRequestDTO(String email,String password) {
}
