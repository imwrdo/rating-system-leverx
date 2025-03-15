package org.leverx.ratingapp.dtos.comments;

public record CommentWithRegistrationRequestDTO(
        String message,
        Integer grade,
        String firstName,
        String lastName,
        String password,
        String email
) {}
