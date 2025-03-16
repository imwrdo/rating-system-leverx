package org.leverx.ratingapp.dtos.user;

import lombok.Builder;
import org.leverx.ratingapp.models.entities.User;
import java.time.LocalDateTime;

/**
 * DTO representing the ranking information of a {@link User}, typically used for displaying
 * the user's rank, name, email, and their rating with respect to other users.
 */
@Builder
public record UserRankingDTO(
        Long place,                  // The user's rank or position in the overall ranking.
        Long id,                     // Unique identifier for the user.
        String firstName,            // The user's first name.
        String lastName,             // The user's last name.
        String email,                // The user's email address.
        LocalDateTime createdAt,     // The timestamp of when the user was created.
        Double rating,               // The rating of the user (usually calculated from comments).
        Integer totalCommentNumber  // The total number of comments received by the user.
) {}
