package org.leverx.ratingapp.dtos.comments;

import lombok.Builder;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.enums.Status;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for representing a comment response.
 * This record encapsulates the details of a comment, including metadata such as
 * author, seller, status, and rating grade.
 */
@Builder
public record CommentResponseDTO(Long id, String message, String author, String seller, String status, Integer grade) {

    /**
     * Converts a list of {@link Comment} entities into a list of CommentResponseDTOs.
     *
     * @param comments The list of {@link Comment} entities to be mapped.
     * @return A list of {@link CommentResponseDTO} objects containing structured comment details.
     */
    public static List<CommentResponseDTO> mapToCommentResponseDTO(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentResponseDTO.builder()
                        .id(comment.getId())
                        .message(comment.getMessage())
                        .author(comment.getAuthor() != null ? comment.getAuthor().getEmail() : "Anonymous")
                        .seller(comment.getSeller().getEmail())
                        .grade(comment.getGrade())
                        .status(comment.getIsApproved()
                                ? Status.APPROVED.getValueOfStatus()
                                : Status.PENDING.getValueOfStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
