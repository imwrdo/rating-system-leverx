package org.leverx.ratingapp.dtos.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.leverx.ratingapp.entities.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private String message;
    private String author;
    private String seller;
    private String Status;

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
