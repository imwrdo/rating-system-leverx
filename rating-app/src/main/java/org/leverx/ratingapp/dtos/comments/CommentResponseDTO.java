package org.leverx.ratingapp.dtos.comments;

import lombok.Builder;
import org.leverx.ratingapp.entities.Comment;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record CommentResponseDTO(String message,String author,String seller,String Status) {

    public static List<CommentResponseDTO> mapToCommentResponseDTO(List<Comment> comments){
        return comments.stream()
                .map(comment -> CommentResponseDTO.builder()
                        .message(comment.getMessage())
                        .author(comment.getAuthor().getEmail())
                        .seller(comment.getSeller().getEmail())
                        .Status(comment.getIs_approved() ? "Approved" : "Pending")
                        .build())
                .collect(Collectors.toList());
    }
}
