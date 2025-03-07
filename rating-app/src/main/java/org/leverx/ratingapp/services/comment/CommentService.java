package org.leverx.ratingapp.services.comment;

import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;

import java.util.List;

public interface CommentService {
     CommentResponseDTO create(Long sellerId, CommentRequestDTO commentObject);
     List<CommentResponseDTO> getAll(Long sellerId);
     CommentResponseDTO getComment(Long sellerId, Long commentId);
     String delete(Long sellerId, Long commentId);
     CommentResponseDTO update(Long sellerId, Long commentId, CommentRequestDTO commentObject);
}
