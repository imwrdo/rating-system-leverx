package org.leverx.ratingapp.dtos.auth;

public record PasswordResetRequestDTO(
    String email,
    String code,
    String newPassword
) {}
