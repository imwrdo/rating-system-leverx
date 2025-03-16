package org.leverx.ratingapp.dtos.comments;

/**
 * DTO for creating a new comment.
 * This record holds the necessary information required to submit a comment.
 */
public record CommentRequestDTO(String message, Integer grade) {
}
