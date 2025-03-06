package org.leverx.ratingapp.dtos.auth;

import lombok.Builder;

@Builder
public record AuthenticationResponseDTO(String user,String token,String Status) {

}
