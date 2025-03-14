package org.leverx.ratingapp.dtos.comments;

public record CommentWithRegistrationRequestDTO(
        String message,
        String firstName,
        String lastName,
        String password,
        String email
) {}
