package org.leverx.ratingapp.dtos.auth;

import lombok.Builder;

/**
 * DTO for authentication responses.
 * This record encapsulates the response data returned after a user authentication attempt.
 */
@Builder
public record AuthenticationResponseDTO(
        String user,
        String token,
        String status)
{}
