package org.leverx.ratingapp.dtos.comments;
/**
 * DTO for submitting a comment along with a user registration request.
 * This record is used when a comment is posted by a user who is not yet registered.
 */
public record CommentWithRegistrationRequestDTO(
        String message,
        Integer grade,
        String firstName,
        String lastName,
        String password,
        String email
) {}
