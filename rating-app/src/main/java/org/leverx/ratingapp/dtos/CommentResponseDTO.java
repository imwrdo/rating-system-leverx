package org.leverx.ratingapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private String message;
    private String author;
    private String seller;
    private String Status;
}
