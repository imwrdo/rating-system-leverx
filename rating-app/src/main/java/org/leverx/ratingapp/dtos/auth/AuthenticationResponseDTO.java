package org.leverx.ratingapp.dtos.auth;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {
    private String user;
    private String token;
    private String Status;
}
