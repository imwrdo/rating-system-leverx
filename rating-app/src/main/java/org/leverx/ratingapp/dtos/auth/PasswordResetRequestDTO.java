package org.leverx.ratingapp.dtos.auth;

import lombok.Builder;

@Builder
public record PasswordResetRequestDTO(
    String email,
    String code,
    String newPassword
) {}
