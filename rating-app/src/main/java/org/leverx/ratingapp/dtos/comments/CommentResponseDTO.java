package org.leverx.ratingapp.dtos.comments;

import lombok.Builder;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.enums.Status;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CommentResponseDTO(Long id, String message, String author, String seller, String status, Integer grade) {

    public static List<CommentResponseDTO> mapToCommentResponseDTO(List<Comment> comments){
        return comments.stream()
                .map(comment -> CommentResponseDTO.builder()
                        .id(comment.getId())
                        .message(comment.getMessage())
                        .author(comment.getAuthor().getEmail())
                        .seller(comment.getSeller().getEmail())
                        .grade(comment.getGrade())
                        .status(comment.getIsApproved()
                                ? Status.APPROVED.getValueOfStatus()
                                : Status.PENDING.getValueOfStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
