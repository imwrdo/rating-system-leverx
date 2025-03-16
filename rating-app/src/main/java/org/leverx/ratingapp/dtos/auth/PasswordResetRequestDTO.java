package org.leverx.ratingapp.dtos.auth;

import lombok.Builder;

/**
 * DTO for password reset requests.
 * This record encapsulates the necessary data for resetting a user's password.
 */
@Builder
public record PasswordResetRequestDTO(
    String email,
    String code,
    String newPassword
) {}
